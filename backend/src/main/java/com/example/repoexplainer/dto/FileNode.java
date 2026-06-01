package com.example.repoexplainer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileNode {

    private String name;

    private String path;

    private String type;

    @Builder.Default
    private List<FileNode> children =
            new ArrayList<>();
}