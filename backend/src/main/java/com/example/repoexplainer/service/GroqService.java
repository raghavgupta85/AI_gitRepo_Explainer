package com.example.repoexplainer.service;

import java.util.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GroqService {

    @Value("${GROQ_API_KEY}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public String generateResponse(String prompt) {

        String url = "https://api.groq.com/openai/v1/chat/completions";

        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON);

        headers.setBearerAuth(apiKey);

        Map<String, Object> body = new HashMap<>();

        body.put("model", "llama3-8b-8192");

        List<Map<String, String>> messages = new ArrayList<>();

        Map<String, String> message = new HashMap<>();

        message.put("role", "user");

        message.put("content", prompt);

        messages.add(message);

        body.put("messages", messages);

        HttpEntity<Map<String, Object>> entity =
                new HttpEntity<>(body, headers);

        ResponseEntity<Map> response =
                restTemplate.exchange(
                        url,
                        HttpMethod.POST,
                        entity,
                        Map.class
                );

        Map choice =
                ((List<Map>) response.getBody().get("choices")).get(0);

        Map messageMap = (Map) choice.get("message");

        return messageMap.get("content").toString();
    }
}