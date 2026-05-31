package com.example.repoexplainer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RepoAnalyzeResponse {

    private String summary;

    private String techStack;

    private String architecture;

    private String setupInstructions;

    private String beginnerExplanation;
}