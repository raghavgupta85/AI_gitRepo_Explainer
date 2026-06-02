package com.example.repoexplainer.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class GeminiService {

    @Value("${GEMINI_API_KEY}")
    private String apiKey;

    private final RestTemplate restTemplate =
            new RestTemplate();

    public String generateExplanation(
            String prompt
    ) {

        String url =
                "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent";

        HttpHeaders headers =
                new HttpHeaders();

        headers.setContentType(
                MediaType.APPLICATION_JSON
        );

        headers.set(
                "x-goog-api-key",
                apiKey
        );

        Map<String, Object> requestBody =
                Map.of(
                        "contents",
                        List.of(
                                Map.of(
                                        "parts",
                                        List.of(
                                                Map.of(
                                                        "text",
                                                        prompt
                                                )
                                        )
                                )
                        )
                );

        HttpEntity<Map<String, Object>> entity =
                new HttpEntity<>(
                        requestBody,
                        headers
                );

        ResponseEntity<Map> response =
                restTemplate.postForEntity(
                        url,
                        entity,
                        Map.class
                );

        return extractText(
                response.getBody()
        );
    }

    private String extractText(
            Map response
    ) {

        List candidates =
                (List) response.get(
                        "candidates"
                );

        Map candidate =
                (Map) candidates.get(0);

        Map content =
                (Map) candidate.get(
                        "content"
                );

        List parts =
                (List) content.get(
                        "parts"
                );

        Map part =
                (Map) parts.get(0);

        return String.valueOf(
                part.get("text")
        );
    }
}