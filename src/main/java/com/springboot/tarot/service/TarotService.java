package com.springboot.tarot.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.dream.dto.OpenAiRequest;
import com.springboot.dream.dto.OpenAiResponse;
import com.springboot.dream.entity.Dream;
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
        // 카테고리랑 랜덤카드 3개 뽑아서 GPT에 건네줌 > 결과 리턴
        //랜덤 숫자 만들어서 그걸로 findById()에 넣어서 카드 3개 출력
        // 랜덤으로 타로 카드 3장 뽑기
        Tarot firstCard = drawRandomTarotCard();
        Tarot secondCard = drawRandomTarotCard();
        Tarot thirdCard = drawRandomTarotCard();

        // 카드 이름과 의미를 결합하여 문자열로 구성
        String firstCardDescription = firstCard.getName() + " - " + firstCard.getMeaning();
        String secondCardDescription = secondCard.getName() + " - " + secondCard.getMeaning();
        String thirdCardDescription = thirdCard.getName() + " - " + thirdCard.getMeaning();

        // 카드의 이름과 의미를 GPT에 전달하여 해석을 받음
       Map<String, Object> chatResponse = responseChatGpt(
                category.getCategoryName(),
                firstCard.getName() + " - " + firstCard.getMeaning(),
                secondCard.getName() + " - " + secondCard.getMeaning(),
                thirdCard.getName() + " - " + thirdCard.getMeaning()
        );

        // 응답 데이터를 Response DTO에 담아 반환
        TarotDto.Response response = new TarotDto.Response();
        response.setCategory(category.getCategoryName());
        response.setFirstCard(firstCardDescription);
        response.setSecondCard(secondCardDescription);
        response.setThirdCard(thirdCardDescription);
        response.setResult((String) chatResponse.get("result"));

        return response;
    }

    private Tarot drawRandomTarotCard() {
        long tarotLength = tarotRepository.count();
        if (tarotLength == 0) {
            throw new IllegalStateException("No Tarot cards available in the database.");
        }
        Random random = new Random();
        // long randomId = random.nextInt((int)tarotLength);
        long randomId = random.nextInt((int)tarotLength) + 1;

        Optional<Tarot> tarotCard = tarotRepository.findById(randomId);
        return tarotCard.orElseThrow(() -> new IllegalArgumentException("Invalid Tarot ID: " + randomId));
    }


    private Map<String, Object> responseChatGpt(String category, String firstCard, String secondCard, String thirdCard) {
        String systemPrompt = "너는 타로술사야. 그리고 고양이 냥체로 말해야 해. 🐾 이모티콘도 꼭 사용해야 해.\n\n" +
                "🔮 오늘의 타로 운세를 해석해줘! \n" +
                "💡 너에게 주어진 카드 정보는 다음과 같아:\n" +
                "1. " + firstCard + "\n" +
                "2. " + secondCard + "\n" +
                "3. " + thirdCard + "\n\n" +
                "📌 해석 작성 규칙:\n" +
                "1. 두괄식으로 시작해서 중요한 내용을 먼저 말해줘.\n" +
                "2. 카드들의 의미를 조합해서 자연스럽고 구체적인 해석을 만들어야 해.\n" +
                "3. 운세를 현실적으로 풀어서 조언해줘. 너무 모호하거나 추상적이면 안 돼.\n" +
                "4. 안 좋은 일이라도 그대로 솔직하게 말해도 돼! 하지만 해결 방법도 함께 제시해줘.\n\n" +
                "📢 응답 형식 (JSON 형식으로 반환):\n" +
                "아래는 예시야." +
                "{\n" +
                "    \"category\": \"" + category + "\",\n" +
                "    \"firstCard\": \"" + firstCard + "\",\n" +
                "    \"secondCard\": \"" + secondCard + "\",\n" +
                "    \"thirdCard\": \"" + thirdCard + "\",\n" +
                "    \"result\": \"🐾 오늘의 타로 해석을 알려줄게! 🐱✨\\n\\n첫 번째 카드는 '" + firstCard + "', 두 번째 카드는 '" + secondCard + "', 세 번째 카드는 '" + thirdCard + "'.\\n\\n" +
                "이 카드를 보면, 현재 상황에서 '" + firstCard + "'이(가) 중요한 기회를 암시하고 있어. '" + secondCard + "'이(가) 변화를 예고하고 있고, '" + thirdCard + "'이(가) 마지막 결정을 나타내.\\n\\n" +
                "이번 운세의 핵심은 '" + secondCard + "'이(가) 주는 메시지를 잘 이해하는 거야. 너무 서두르지 말고 차분하게 상황을 정리하면 좋은 결과로 이어질 거야! 😺✨\"\n" +
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
            responseMap.put("tarotCategory", rootNode.path("category").asText());
            responseMap.put("firstCardMeaning", rootNode.path("firstCardMeaning").asText());
            responseMap.put("secondCardMeaning", rootNode.path("secondCardMeaning").asText());
            responseMap.put("thirdCardMeaning", rootNode.path("thirdCardMeaning").asText());
            responseMap.put("result", rootNode.path("result").asText());
        } catch (Exception e) {
            responseMap.put("error", "Failed to parse response");
            e.printStackTrace();
        }
        return responseMap;
    }
}

