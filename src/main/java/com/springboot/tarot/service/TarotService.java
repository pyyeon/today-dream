package com.springboot.tarot.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.dream.dto.OpenAiRequest;
import com.springboot.dream.dto.OpenAiResponse;
import com.springboot.tarot.dto.TarotDto;
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

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional
    public TarotDto.Response playTarot(TarotCategory category) {
        // 🔮 랜덤으로 타로 카드 3장 뽑기
        Tarot firstCard = drawRandomTarotCard();
        Tarot secondCard = drawRandomTarotCard();
        Tarot thirdCard = drawRandomTarotCard();

        // 🎴 카드 정보 저장
        String firstCardName = firstCard.getName();
        String firstCardMeaning = firstCard.getMeaning();

        String secondCardName = secondCard.getName();
        String secondCardMeaning = secondCard.getMeaning();

        String thirdCardName = thirdCard.getName();
        String thirdCardMeaning = thirdCard.getMeaning();

        // 🎯 GPT에게 카드 정보를 보내고 해석을 요청
        Map<String, Object> chatResponse = responseChatGpt(
                category.getCategoryName(),
                firstCardName, firstCardMeaning,
                secondCardName, secondCardMeaning,
                thirdCardName, thirdCardMeaning
        );

        // 응답 데이터를 Response DTO에 담아 반환
        TarotDto.Response response = new TarotDto.Response();
        response.setCategory(category.getCategoryName());
        response.setSummary((String) chatResponse.get("summary"));  // 요약된 운세
        response.setFirstCard(firstCardName);
        response.setFirstCardMeaning(firstCardMeaning);
        response.setSecondCard(secondCardName);
        response.setSecondCardMeaning(secondCardMeaning);
        response.setThirdCard(thirdCardName);
        response.setThirdCardMeaning(thirdCardMeaning);
        response.setResult((String) chatResponse.get("result"));  // 상세 해석

        return response;
    }

    private Tarot drawRandomTarotCard() {
        long tarotLength = tarotRepository.count();
        if (tarotLength == 0) {
            throw new IllegalStateException("No Tarot cards available in the database.");
        }
        Random random = new Random();
        long randomId = random.nextInt((int) tarotLength) + 1;

        Optional<Tarot> tarotCard = tarotRepository.findById(randomId);
        return tarotCard.orElseThrow(() -> new IllegalArgumentException("Invalid Tarot ID: " + randomId));
    }

    private Map<String, Object> responseChatGpt(String category,
                                                String firstCard, String firstMeaning,
                                                String secondCard, String secondMeaning,
                                                String thirdCard, String thirdMeaning) {
        String systemPrompt = "너는 타로술사야. 그리고 고양이 냥체로 말해야 해. 🐾 이모티콘도 꼭 사용해야 해.\n\n" +
                "🔮 오늘의 타로 운세를 해석해줘! \n" +
                "💡 너에게 주어진 카드 정보는 다음과 같아:\n\n" +
                "첫 번째 카드: " + firstCard + " - " + firstMeaning + "\n" +
                "두 번째 카드: " + secondCard + " - " + secondMeaning + "\n" +
                "세 번째 카드: " + thirdCard + " - " + thirdMeaning + "\n\n" +
                "📌 **응답 형식(JSON)으로 다음처럼 작성해줘:**\n" +
                "{\n" +
                "    \"category\": \"" + category + "\",\n" +
                "    \"summary\": \"운세 요약 한 줄을 여기에 입력해줘. 🐾😺✨\",\n" +
                "    \"result\": \"상세 해석을 여기에 작성해줘. 첫 문장은 두괄식으로 간단한 요약을 포함하고, 이어서 현실적인 조언을 포함해야 해.\"\n" +
                "}";

        // OpenAI API 요청 생성
        OpenAiRequest request = new OpenAiRequest("gpt-4o", systemPrompt, "");
        OpenAiResponse response = template.postForObject(apiURL, request, OpenAiResponse.class);

        if (response != null && response.getChoices() != null && !response.getChoices().isEmpty()) {
            String responseContent = response.getChoices().get(0).getMessage().getContent();
            return parseResponse(responseContent);
        }
        return null;
    }


    private Map<String, Object> parseResponse(String content) {
        Map<String, Object> responseMap = new HashMap<>();
        try {
            JsonNode rootNode = objectMapper.readTree(content);
            responseMap.put("category", rootNode.path("category").asText());

            // 🛠 `summary` 변수를 확실하게 설정
            String summaryText = rootNode.path("summary").asText();
            if (summaryText == null || summaryText.isEmpty()) {
                summaryText = "운세 요약이 없습니다. 😺✨";
            }
            responseMap.put("summary", summaryText);

            responseMap.put("result", rootNode.path("result").asText());
        } catch (Exception e) {
            responseMap.put("error", "Failed to parse response");
            e.printStackTrace();
        }
        return responseMap;
    }


}
