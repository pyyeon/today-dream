package com.springboot.dream.dto;


import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class OpenAiRequest {
    private String model;
    private List<Message> messages;

    public OpenAiRequest(String model, String systemPrompt, String userPrompt){
        this.model = model;
        this.messages = new ArrayList<>();
        if (systemPrompt != null && !systemPrompt.isEmpty()) {
            this.messages.add(new Message("system", systemPrompt));
        }
        this.messages.add(new Message("user", userPrompt));
    }
}
