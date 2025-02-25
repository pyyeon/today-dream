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
                "💡 너에게 주어진 카드 정보는 다음과 같아:\n\n" +
                "첫 번째 카드: " + firstCard + "\n" +
                "두 번째 카드: " + secondCard + "\n" +
                "세 번째 카드: " + thirdCard + "\n\n" +
                "📌 해석 작성 규칙:\n" +
                "1. **운세를 한 줄로 요약한 해석을 첫 문장에 넣어줘.**\n" +
                "2. 첫 번째, 두 번째, 세 번째 카드의 의미를 나열한 후 줄내림을 해줘.\n" +
                "3. 카드들의 의미를 종합하여 자연스럽고 구체적인 해석을 만들어야 해.\n" +
                "4. 운세는 두괄식으로 시작하고, 구체적인 설명과 함께 현실적인 조언을 포함해야 해.\n" +
                "5. 모호하거나 추상적인 해석은 피하고, 실제 상황에 적용할 수 있는 조언을 줘야 해.\n" +
                "6. 운세가 부정적이라면 조심해야 할 점과 해결 방법을 함께 설명해줘.\n\n" +
                "📢 응답 형식 (JSON 형식으로 반환):\n" +
                "{\n" +
                "    \"category\": \"" + category + "\",\n" +
                "    \"firstCard\": \"" + firstCard + "\",\n" +
                "    \"secondCard\": \"" + secondCard + "\",\n" +
                "    \"thirdCard\": \"" + thirdCard + "\",\n" +
                "    \"result\": \"🐾 [운세 요약 한 줄] 😺✨\\n\\n" +
                "첫 번째 카드는 '" + firstCard + "'\\n" +
                "두 번째 카드는 '" + secondCard + "'\\n" +
                "세 번째 카드는 '" + thirdCard + "'\\n\\n" +
                "이 조합을 보면, 현재 네 상황에서 중요한 메시지를 주고 있어! 첫 번째 카드는 현재 상태를 나타내고, 두 번째 카드는 변화를 예고하며, 세 번째 카드는 최종적인 방향을 가리키고 있어.\\n\\n" +
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

