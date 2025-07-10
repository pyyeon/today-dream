package com.springboot.tarot.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.dream.dto.OpenAiRequest;
import com.springboot.dream.dto.OpenAiResponse;
import com.springboot.tarot.entity.Tarot;
import com.springboot.tarot.entity.TarotCategory;
import com.springboot.tarot.repository.TarotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Transactional
@Service
public class TarotService {

    @Value("${openai.model}")
    private String model;

    @Value("${openai.api.url}")
    private String apiURL;

    @Autowired
    private RestTemplate template;

    @Autowired
    private TarotRepository tarotRepository;

    @Autowired
    private ObjectMapper objectMapper;

    public List<Tarot> playTarot(TarotCategory category) {
        // 랜덤 카드 3장 뽑기
        List<Tarot> cards = drawRandomTarotCards(3);

        // 카드 정보 추출
        String firstCardName = cards.get(0).getName();
        String firstCardMeaning = cards.get(0).getMeaning();
        String secondCardName = cards.get(1).getName();
        String secondCardMeaning = cards.get(1).getMeaning();
        String thirdCardName = cards.get(2).getName();
        String thirdCardMeaning = cards.get(2).getMeaning();

        // GPT 요청
        Map<String, Object> chatResponse = responseChatGpt(
                category.getCategoryName(),
                firstCardName, firstCardMeaning,
                secondCardName, secondCardMeaning,
                thirdCardName, thirdCardMeaning
        );

        // 여기서부터는 컨트롤러에서 매핑하여 DTO로 바꾸도록 한다
        // 서비스는 카드 정보만 반환
        return cards;
    }

    private List<Tarot> drawRandomTarotCards(int count) {
        List<Long> allIds = tarotRepository.findAllIds(); // ID 목록 조회
        if (allIds.size() < count) {
            throw new IllegalStateException("타로 카드가 부족합니다.");
        }
        Collections.shuffle(allIds);
        List<Long> selectedIds = allIds.subList(0, count);
        List<Tarot> cards = tarotRepository.findAllById(selectedIds);
        if (cards.size() != count) {
            throw new IllegalArgumentException("카드 조회에 실패했습니다.");
        }
        return cards;
    }

    private Map<String, Object> responseChatGpt(String category,
                                                String firstCard, String firstMeaning,
                                                String secondCard, String secondMeaning,
                                                String thirdCard, String thirdMeaning) {

        String systemPrompt = "너는 타로술사야. 그리고 고양이 냥체로 말해야 해. 🐾 이모티콘도 꼭 사용해야 해.\n\n" +
                "오늘의 타로 운세를 해석해줘! 주제에 맞게 해석해줘야해. \n" +
                "알아듣기 쉽게 직관적으로 먼저 두괄식으로 설명해주고 조언도 같이 말해줘.  \n" +
                "너에게 주어진 카드 정보는 다음과 같아:\n\n" +
                "주제 : " + category +  "\n\n" +
                "첫 번째 카드: " + firstCard + " - " + firstMeaning + "\n" +
                "두 번째 카드: " + secondCard + " - " + secondMeaning + "\n" +
                "세 번째 카드: " + thirdCard + " - " + thirdMeaning + "\n\n" +
                "**응답 형식(JSON)으로 다음처럼 작성해줘:**\n" +
                "{\n" +
                "  \"category\": \"" + category + "\",\n" +
                "  \"summary\": \"운세 요약 한 줄을 여기에 입력해줘.\",\n" +
                "  \"result\": \"상세 해석을 여기에 작성해줘.\"\n" +
                "}";

        OpenAiRequest request = new OpenAiRequest(model, systemPrompt, "");
        OpenAiResponse response = template.postForObject(apiURL, request, OpenAiResponse.class);

        if (response != null && response.getChoices() != null && !response.getChoices().isEmpty()) {
            String content = response.getChoices().get(0).getMessage().getContent();
            return parseResponse(content);
        }

        return Map.of("error", "GPT 응답이 없습니다.");
    }

    private Map<String, Object> parseResponse(String content) {
        Map<String, Object> responseMap = new HashMap<>();
        try {
            JsonNode rootNode = objectMapper.readTree(content);
            responseMap.put("category", rootNode.path("category").asText());

            String summary = rootNode.path("summary").asText();
            if (summary == null || summary.isEmpty()) {
                summary = "운세 요약이 없습니다.";
            }
            responseMap.put("summary", summary);

            responseMap.put("result", rootNode.path("result").asText());
        } catch (Exception e) {
            responseMap.put("error", "GPT 응답 파싱 실패");
            e.printStackTrace();
        }
        return responseMap;
    }

    // 선택적으로 GPT 결과만 따로 반환하는 메서드도 제공 가능
    public Map<String, Object> getTarotInterpretation(TarotCategory category, List<Tarot> cards) {
        if (cards.size() != 3) throw new IllegalArgumentException("카드는 3장이어야 합니다.");

        return responseChatGpt(
                category.getCategoryName(),
                cards.get(0).getName(), cards.get(0).getMeaning(),
                cards.get(1).getName(), cards.get(1).getMeaning(),
                cards.get(2).getName(), cards.get(2).getMeaning()
        );
    }
}
