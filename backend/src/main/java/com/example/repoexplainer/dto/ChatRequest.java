package com.example.repoexplainer.dto;

import lombok.Data;

@Data
public class ChatRequest {

    private String repoUrl;

    private String question;

    private boolean repositoryMode;
}