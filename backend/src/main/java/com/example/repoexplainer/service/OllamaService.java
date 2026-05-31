package com.example.repoexplainer.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class OllamaService {

    private static final String OLLAMA_API_URL =
            "http://localhost:11434/api/generate";

    private final RestTemplate restTemplate;

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

        Map<String, Object> requestBody =
                Map.of(
                        "model", "llama3.2:1b",
                        "prompt", prompt,
                        "stream", false
                );

        Map response =
                restTemplate.postForObject(
                        OLLAMA_API_URL,
                        requestBody,
                        Map.class
                );

        return String.valueOf(
                response.get("response")
        );
    }

    public String generateChatResponse(
            String prompt
    ) {

        String finalPrompt =
                """
                You are a helpful AI assistant.

                Rules:
                - Give direct natural answers.
                - Do NOT generate SUMMARY, TECH_STACK, ARCHITECTURE.
                - Do NOT use markdown.
                - Keep answers short.
                - Maximum 5 lines.

                User Question:
                %s
                """
                        .formatted(prompt);

        Map<String, Object> requestBody =
                Map.of(
                        "model", "llama3.2:1b",
                        "prompt", finalPrompt,
                        "stream", false
                );

        Map response =
                restTemplate.postForObject(
                        OLLAMA_API_URL,
                        requestBody,
                        Map.class
                );

        return String.valueOf(
                response.get("response")
        ).trim();
    }
}

// i am raghav