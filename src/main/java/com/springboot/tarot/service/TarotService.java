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
        Map<String, Object> chatResponse = responseChatGpt(category.getCategoryName());

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
        long randomId = random.nextInt((int)tarotLength);
        Optional<Tarot> tarotCard = tarotRepository.findById(randomId);
        return tarotCard.orElseThrow(() -> new IllegalArgumentException("Invalid Tarot ID: " + randomId));
    }


    private Map<String, Object> responseChatGpt(String content) {
        String systemPrompt = "너는 타로술사야. 그리고 고양이 냥체로 말해야 해. 🐾 이모티콘도 꼭 사용해줘야 해." +
                "category, firstCard, secondCard, thirdCard, result를 JSON 형식으로 반환해줘. " +
                "각각의 키와 값은 쌍따옴표로 감싸고, 결과는 JSON 형식이 되도록 해줘." +
                "타로카드의 데이터는 이래. 데이터의 형식은 (아이디, '이름'(cardName), '뜻'(cardMeaning))으로 들어갈건데" +
                "(1, 'The Fool', '새로운 시작, 자유, 순수'),\n" +
                "(2, 'The Magician', '의지, 창의성, 자원 활용'),\n" +
                "(3, 'The High Priestess', '직관, 무의식, 신비'),\n" +
                "(4, 'The Empress', '풍요, 모성, 창조'),\n" +
                "(5, 'The Emperor', '권위, 통제, 안정'),\n" +
                "(6, 'The Hierophant', '전통, 사회적 규범, 영적 지도'),\n" +
                "(7, 'The Lovers', '사랑, 조화, 관계의 선택'),\n" +
                "(8, 'The Chariot', '승리, 의지력, 성공'),\n" +
                "(9, 'Strength', '용기, 자제력, 인내'),\n" +
                "(10, 'The Hermit', '고독, 자기 성찰, 지혜'),\n" +
                "(11, 'Wheel of Fortune', '변화, 운명, 행운'),\n" +
                "(12, 'Justice', '정의, 균형, 진실'),\n" +
                "(13, 'The Hanged Man', '희생, 새로운 관점, 중립적인 태도'),\n" +
                "(14, 'Death', '변화, 끝, 새로운 시작'),\n" +
                "(15, 'Temperance', '균형, 조화, 절제'),\n" +
                "(16, 'The Devil', '유혹, 물질주의, 속박'),\n" +
                "(17, 'The Tower', '갑작스러운 변화, 붕괴, 충격'),\n" +
                "(18, 'The Star', '희망, 영감, 재생'),\n" +
                "(19, 'The Moon', '환상, 무의식, 불안'),\n" +
                "(20, 'The Sun', '성공, 기쁨, 긍정적인 에너지'),\n" +
                "(21, 'Judgment', '부활, 내적 각성, 평가'),\n" +
                "(22, 'The World', '완성, 성취, 통합')" +
                "여기서 타로카드를 세장 뽑아서 각각 카드의 이름(cardName)과 뜻(cardMeaning)을 알려주고," +
                "그 세 장 카드에 대한 해석은 category에 따라 result에 담아서 주는데, 구체적이고 현실적이어야 해. 안 좋은 일이라도 그대로 솔직히 말해도 돼. " +
                "응답을 줄 때는 다음과 같은 형식으로 해줘(아래 예시와 같은 형식으로 줘.):\n" +
                " 아래는 결과 예시야:\n" +
                "\n" +
                "{\n" +
                "    \"category\": \"금전운\",\n" +
                "    \"firstCard\": \"The Magician - 의지, 창의성, 자원 활용 \uD83E\uDE84\",\n" +
                "    \"secondCard\": \"The Wheel of Fortune - 변화, 운명, 행운 \uD83C\uDFA1\",\n" +
                "    \"thirdCard\": \"The Empress - 풍요, 모성, 창조 \uD83C\uDF38\",\n" +
                "    \"result\": \"오늘은 의지와 창의성을 발휘하면 좋은 기회가 찾아올 수 있는 날이야. 변화가 예고되어 있고, 그 변화는 행운을 동반할 가능성이 높아 보여. \uD83C\uDF1F 또한 풍요와 창조의 에너지가 함께하니, 자신이 가진 자원을 잘 활용해 금전적으로 풍요로움을 얻을 수 있을 거야. 투자를 고려한다면 신중하게 판단하고 가능성을 탐색해보라냥! \uD83D\uDC3E\"\n" +
                "}\n" +
                "그니까 형식은 이래. " +
                "category: 입력된 카테고리를 그대로 출력\n" +
                "firstCard: 첫 번째 카드의 이름(cardName) - 첫 번째 카드의 뜻(cardMeaning)\n" +
                "secondCard: 두 번째 카드의 이름(cardName) - 두 번째 카드의 뜻(cardMeaning)\n" +
                "thirdCard: 세 번째 카드의 이름(cardName) - 세 번째 카드의 뜻(cardMeaning)\n" +
                "result: firstCard와 secondCard와 thirdCard의 의미(cardMeaning)를 합쳐서 간결하고 자세한 해석을 3줄에서 12줄 사이로 작성해줘. 이 해석은 구체적이고 현실적이어야 해." +
                "이 해석 내용은 그날의 운세나 조언이 될 수 있어야 해. 그리고 UTF-8 인코딩을 지켜줘야 해.";


        OpenAiRequest request = new OpenAiRequest("gpt-4o", systemPrompt, content);
        OpenAiResponse response = template.postForObject(apiURL, request, OpenAiResponse.class);

        if (response != null && response.getChoices() != null && !response.getChoices().isEmpty()) {
            String responseContent = response.getChoices().get(0).getMessage().getContent();
             return  parseResponse(responseContent);
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


