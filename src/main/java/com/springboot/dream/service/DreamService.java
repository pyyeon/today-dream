package com.springboot.dream.service;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.auth.jwt.JwtTokenizer;
import com.springboot.auth.service.AuthService;
import com.springboot.dream.dto.OpenAiRequest;
import com.springboot.dream.dto.OpenAiResponse;
import com.springboot.dream.entity.Dream;
import com.springboot.dream.entity.DreamKeyword;
import com.springboot.dream.entity.View;
import com.springboot.dream.repository.DreamRepository;
import com.springboot.dream.repository.ViewRepository;
import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import com.springboot.interpretation.entity.Interpretation;
import com.springboot.interpretation.entity.Interpretation_Mood_Keyword;
import com.springboot.member.entity.Member;
import com.springboot.member.service.MemberService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;


import java.time.LocalDate;
import java.time.LocalDateTime;

import java.util.*;

@Transactional
@Service
public class DreamService {

    @Value("${openai.model}")
    private String model;

    @Value("${openai.api.url}")
    private String apiURL;

    @Autowired
    private RestTemplate template;

    private final ObjectMapper objectMapper = new ObjectMapper();


    private final DreamRepository dreamRepository;
    private final MemberService memberService;
    private final ViewRepository viewRepository;
    private final JwtTokenizer jwtTokenizer;

    @Getter
    @Value("${jwt.key}")
    private String secretKey;

    public DreamService(DreamRepository dreamRepository, MemberService memberService, ViewRepository viewRepository, JwtTokenizer jwtTokenizer) {
        this.dreamRepository = dreamRepository;
        this.memberService = memberService;
        this.viewRepository = viewRepository;
        this.jwtTokenizer = jwtTokenizer;
    }

    public Dream createDream(Dream dream, Authentication authentication){

        String email = null;
        if (authentication != null) {
            email = authentication.getName();
            Member member = memberService.findVerifiedMember(email);
            dream.setMember(member);
        }

        Map<String, Object> chatResponse = responseChatGpt(dream.getContent());

        chatResponse.get("dream_keyword"); // List<String>

        List<DreamKeyword> dreamKeywords = new ArrayList<>();
        List<String> dreamString = (List<String>) chatResponse.get("dream_keyword");
        for(String keyword : dreamString){
            DreamKeyword dreamKeyword = new DreamKeyword();
            dreamKeyword.setName(keyword);
            dreamKeyword.setDream(dream);
            dreamKeywords.add(dreamKeyword);
        }
        dream.setDreamKeywords(dreamKeywords);
        Interpretation interpretation = new Interpretation();
        interpretation.setContent((String) chatResponse.get("content"));
        interpretation.setSummary((String) chatResponse.get("summary"));
        interpretation.setAdvice((String)chatResponse.get("advice"));
        Interpretation_Mood_Keyword keyword = new Interpretation_Mood_Keyword();
        keyword.setName((String)chatResponse.get("interpretation_mood_keyword"));
        interpretation.setKeyword(keyword);
        dream.setInterpretation(interpretation);

        Dream saveDream = dreamRepository.save(dream);

        return saveDream;
    }
    public Dream updateDream(Dream dream, String email){
        Dream findDream = findVerifiedDream(dream.getDreamId());
        memberService.findVerifiedMember(email);
        if(!findDream.getMember().getEmail().equals(email)){
            throw new BusinessLogicException(ExceptionCode.NOT_YOUR_DREAM);
        }

        Optional.ofNullable(dream.getDreamSecret())
                .ifPresent(dreamSecret -> findDream.setDreamSecret(dreamSecret));

        findDream.setModifiedAt(LocalDateTime.now());

        return dreamRepository.save(findDream);
    }

    public Dream findDream(long dreamId) {
        Dream findDream = findVerifiedDream(dreamId);
        if(findDream.getDreamSecret() == Dream.DreamSecret.DREAM_PRIVATE){
            throw new BusinessLogicException(ExceptionCode.DREAM_IS_PRIVATE);
        }else if( findDream.getDreamStatus() == Dream.DreamStatus.DREAM_DEACTIVE){
            throw new BusinessLogicException(ExceptionCode.DREAM_NOT_FOUND);
        }else{
            return findDream;
        }
    }

    public Dream findDreamWithAuthCheck(long dreamId, String authorizationHeader) {
        String email = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")){
            String token = authorizationHeader.substring(7);
            Jws<Claims> claims = jwtTokenizer.getClaims(token, jwtTokenizer.encodeBase64SecretKey(secretKey));
            email = (String) claims.getBody().get("username");
            return findDream(dreamId, email);
        }else{
            return findDream(dreamId);
        }
    }

    public Dream findDream(long dreamId, String email) {
        Dream findDream = findVerifiedDream(dreamId);
        Member findMember = memberService.findVerifiedMember(email);
        Optional<View> optionalView = viewRepository.findByDreamAndMember(findDream, findMember);
        if(findDream.getDreamSecret().equals(Dream.DreamSecret.DREAM_PRIVATE)) {
            if(!email.equals(findDream.getMember().getEmail())) {
                throw new BusinessLogicException(ExceptionCode.DREAM_IS_PRIVATE);
            }
        }

        if(findDream.getDreamStatus() == Dream.DreamStatus.DREAM_DEACTIVE) {
            throw new BusinessLogicException(ExceptionCode.DREAM_NOT_FOUND);
        }

        if(optionalView.isEmpty()) {
            View view = createView(dreamId, email);
            findDream.addView(view);
        }

        findDream.setViewCount(findDream.getViews().size());
        return findDream;
    }

    public Dream findVerifiedDream(long dreamId) {
        Optional<Dream> optionalOrder = dreamRepository.findById(dreamId);
        Dream findOrder =
                optionalOrder.orElseThrow(() ->
                        new BusinessLogicException(ExceptionCode.DREAM_NOT_FOUND));
        return findOrder;
    }

    public Page<Dream> findDreamsVerify(String dreamKeyword, int page, int size){
        if(dreamKeyword == null || dreamKeyword.isEmpty()) {
            return findAllDreamsPublic(page, size);
        }else{
            return findDreams(dreamKeyword,page,size);
        }
    }

    public Page<Dream> findDreams(String dreamKeyword, int page, int size) {
        return dreamRepository.findByDreamStatusAndDreamSecretAndDreamKeywords_NameContaining(Dream.DreamStatus.DREAM_ACTIVE, Dream.DreamSecret.DREAM_PUBLIC, dreamKeyword, PageRequest.of(page, size, Sort.by("dreamId").descending()));
    }

    public Page<Dream> findAllDreams(int page, int size){
        return dreamRepository.findByDreamStatus(Dream.DreamStatus.DREAM_ACTIVE,PageRequest.of(page, size,
                Sort.by("dreamId").descending()));
    }

    public Page<Dream> findAllDreamsPublic(int page, int size){
        return dreamRepository.findByDreamStatusAndDreamSecret(Dream.DreamStatus.DREAM_ACTIVE, Dream.DreamSecret.DREAM_PUBLIC, PageRequest.of(page, size,
                Sort.by("dreamId").descending()));
    }


    public void deleteDream(long dreamId,String email){
        Dream findDream = findVerifiedDream(dreamId);
        if(!findDream.getMember().getEmail().equals(email)){
            throw new BusinessLogicException(ExceptionCode.NOT_YOUR_DREAM);
        }
        findDream.setDreamStatus(Dream.DreamStatus.DREAM_DEACTIVE);
        dreamRepository.save(findDream);
    }

    private View createView(long dreamId, String email){
        Dream dream = findVerifiedDream(dreamId);
        Member member = memberService.findVerifiedMember(email);

        View view = new View();
        view.setMember(member);
        view.setDream(dream);

        return viewRepository.save(view);


    }



    private Map<String, Object> parseResponse(String content) {
        Map<String, Object> responseMap = new HashMap<>();
        try {
            JsonNode rootNode = objectMapper.readTree(content);
            String content_inter = rootNode.path("content").asText();
            String summary = rootNode.path("summary").asText();
            String advice = rootNode.path("advice").asText();
            String interpretationMoodKeyword = rootNode.path("interpretation_mood_keyword").asText();

            // dream_keyword 디버깅
            List<String> dreamKeywords = new ArrayList<>();
            JsonNode dreamKeywordNode = rootNode.path("dream_keyword");
            if (dreamKeywordNode.isArray()) {
                for (JsonNode keywordNode : dreamKeywordNode) {
                    dreamKeywords.add(keywordNode.asText());
                    System.out.println("Parsed keyword: " + keywordNode.asText());
                }
            } else {
                System.out.println("dream_keyword is not an array or is missing.");
            }
            responseMap.put("content", content_inter);
            responseMap.put("summary", summary);
            responseMap.put("advice", advice);
            responseMap.put("interpretation_mood_keyword", interpretationMoodKeyword);
            responseMap.put("dream_keyword", dreamKeywords);
        } catch (Exception e) {
            responseMap.put("error", "Failed to parse response");
            e.printStackTrace();
        }
        return responseMap;
    }

    private Map<String, Object>responseChatGpt(String content){


        String systemPrompt = "너는 꿈 해몽가야. 모든 말은 고양이 냥체를 해줘." +
                "이모티콘도 써야 해. 긍정적으로 말해줘.응답을 줄 때는 dream_keyword, content, summary, advice, interpretation_mood_keyword의 json으로 주는데 json이라고 표시는 하지마. " +
                "UTF-8 인코딩을 지켜줘. 꿈 키워드는 2개 배열로 주는데 하나는 감정 하나는 사물 관련해서 dream_keyword에 담아서 줘 " +
                "해몽 내용은 content, 해몽 내용을 요약해서 3줄안으로 summary에, 1줄 조언은 advice에 넣어주고, 해몽 분위기 키워드(희망" +
                "성취," +
                "치유," +
                "기회," +
                "성장," +
                "평화," +
                "행복," +
                "새로운 시작," +
                "사랑," +
                "보호," +
                "조화," +
                "번영," +
                "용기," +
                "지혜," +
                "기쁨," +
                "행운)에서 1개를 interpretation_mood_keyword에 담아야 해, 근데 너가 응답을 못 해줄 것 같은 내용에는 Error를 보내줘";

        OpenAiRequest request = new OpenAiRequest("gpt-4o", systemPrompt, content);
        OpenAiResponse response = template.postForObject(apiURL, request, OpenAiResponse.class);

        if (response != null && response.getChoices() != null && !response.getChoices().isEmpty()) {
            String responseContent = response.getChoices().get(0).getMessage().getContent();

            if (responseContent.contains("Error") || responseContent.trim().isEmpty()) {
                throw new BusinessLogicException(ExceptionCode.NOT_CONTENT);
            }

            Map<String, Object> responseMap = parseResponse(responseContent);
            return responseMap;
        }else{
            throw new BusinessLogicException(ExceptionCode.NOT_CONTENT);
        }
    }

}