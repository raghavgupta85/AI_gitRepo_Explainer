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

    private final RestTemplate restTemplate =
            new RestTemplate();

    public String generateResponse(
            String prompt
    ) {

        String url =
                "https://api.groq.com/openai/v1/chat/completions";

        HttpHeaders headers =
                new HttpHeaders();

        headers.setContentType(
                MediaType.APPLICATION_JSON
        );

        headers.setBearerAuth(
                apiKey
        );

        String finalPrompt =
                """
                STRICT RULES:
                - Return plain text only
                - No markdown
                - No bold text
                - No bullet points
                - Do not use **
                - Follow exact formatting
                - Do not add extra headings

                %s
                """
                        .formatted(prompt);

        Map<String, Object> body =
                new HashMap<>();

        body.put(
                "model",
                "llama-3.1-8b-instant"
        );

        List<Map<String, String>> messages =
                new ArrayList<>();

        Map<String, String> message =
                new HashMap<>();

        message.put(
                "role",
                "user"
        );

        message.put(
                "content",
                finalPrompt
        );

        messages.add(message);

        body.put(
                "messages",
                messages
        );

        HttpEntity<Map<String, Object>> entity =
                new HttpEntity<>(
                        body,
                        headers
                );

        ResponseEntity<Map> response =
                restTemplate.exchange(
                        url,
                        HttpMethod.POST,
                        entity,
                        Map.class
                );

        Map choice =
                ((List<Map>)
                        response.getBody()
                                .get("choices"))
                        .get(0);

        Map messageMap =
                (Map) choice.get("message");

        String content =
                messageMap.get("content")
                        .toString();

        content =
                content.replace("**", "");

        content =
                content.replace("##", "");

        return content.trim();
    }
}

