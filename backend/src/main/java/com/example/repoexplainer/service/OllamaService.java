
package com.example.repoexplainer.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class OllamaService {

    private final RestTemplate restTemplate;

    @Value("${groq.api.key}")
    private String groqApiKey;

    public OllamaService(
            RestTemplate restTemplate
    ) {

        this.restTemplate = restTemplate;
    }

    public String generateExplanation(
            String repositoryContent
    ) {

        String prompt =
                """
                You are an expert GitHub repository analyzer.

                Your task:
                - Explain the repository clearly.
                - Ignore badges, markdown, links, URLs, images.
                - Keep answers short and clean.
                - Maximum 3-4 lines per section.

                Return ONLY in this exact format:

                SUMMARY:
                <answer>

                TECH_STACK:
                <answer>

                ARCHITECTURE:
                <answer>

                SETUP:
                <answer>

                BEGINNER_EXPLANATION:
                <answer>

                Repository Content:
                %s
                """
                        .formatted(repositoryContent);

        return callGroq(prompt);
    }

    public String generateChatResponse(
            String prompt
    ) {

        String finalPrompt =
                """
                You are an expert repository AI assistant.

                Rules:
                - Answer naturally.
                - Use repository context only.
                - Mention file names if possible.
                - Keep answers short.
                - Maximum 5 lines.

                User Question:
                %s
                """
                        .formatted(prompt);

        return callGroq(finalPrompt);
    }

    private String callGroq(
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
                groqApiKey
        );

        Map<String, Object> requestBody =
                Map.of(
                        "model", "llama-3.1-8b-instant",
                        "messages", List.of(
                                Map.of(
                                        "role", "user",
                                        "content", prompt
                                )
                        )
                );

        HttpEntity<Map<String, Object>> entity =
                new HttpEntity<>(
                        requestBody,
                        headers
                );

        Map response =
                restTemplate.postForObject(
                        url,
                        entity,
                        Map.class
                );

        List choices =
                (List) response.get("choices");

        Map firstChoice =
                (Map) choices.get(0);

        Map message =
                (Map) firstChoice.get("message");

        return String.valueOf(
                message.get("content")
        ).trim();
    }
}
