package com.example.repoexplainer.service;

import com.example.repoexplainer.util.GitHubUrlParser;
import com.example.repoexplainer.util.ReadmeCleaner;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

import com.example.repoexplainer.dto.FileNode;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

@Service
public class GitHubService {

    private final RestTemplate restTemplate;

    private final OllamaService ollamaService;

    public GitHubService(
            RestTemplate restTemplate,
            OllamaService ollamaService
    ) {

        this.restTemplate = restTemplate;
        this.ollamaService = ollamaService;
    }

    public String getRepositoryInfo(
            String repoUrl
    ) {

        String repositoryContent =
                getRepositoryContent(repoUrl);

        return ollamaService.generateExplanation(
                repositoryContent
        );
    }

    public List<FileNode> getRepositoryFiles(
        String repoUrl
) {

    String[] parts =
            GitHubUrlParser.extractOwnerAndRepo(
                    repoUrl
            );

    String owner = parts[0];

    String repo = parts[1];

    String contentsApiUrl =
            "https://api.github.com/repos/"
                    + owner
                    + "/"
                    + repo
                    + "/git/trees/main?recursive=1";

    Map<String, Object> response =
            restTemplate.getForObject(
                    contentsApiUrl,
                    Map.class
            );

    List<Map<String, Object>> tree =
            (List<Map<String, Object>>)
                    response.get("tree");

    FileNode root =
            FileNode.builder()
                    .name("root")
                    .type("dir")
                    .build();

    for (Map<String, Object> item : tree) {

        String path =
                String.valueOf(
                        item.get("path")
                );

        String type =
                String.valueOf(
                        item.get("type")
                );

        addPathToTree(
                root,
                path,
                type
        );
    }

    return root.getChildren();
}


private void addPathToTree(

        FileNode root,

        String fullPath,

        String githubType
) {

    String[] parts =
            fullPath.split("/");

    FileNode current =
            root;

    String currentPath =
            "";

    for (int i = 0; i < parts.length; i++) {

        String part = parts[i];

        currentPath +=
                currentPath.isEmpty()
                        ? part
                        : "/" + part;

        boolean isLast =
                i == parts.length - 1;

        String nodeType =
                isLast
                        ? (
                        githubType.equals("tree")
                                ? "dir"
                                : "file"
                )
                        : "dir";

        FileNode existing =
                current.getChildren()

                        .stream()

                        .filter(node ->

                                node.getName()
                                        .equals(part)
                        )

                        .findFirst()

                        .orElse(null);

        if (existing == null) {

            existing =
                    FileNode.builder()

                            .name(part)

                            .path(currentPath)

                            .type(nodeType)

                            .build();

            current.getChildren()
                    .add(existing);
        }

        current = existing;
    }
}

public String getFileContent(
        String repoUrl,
        String filePath
) {

    try {

        String[] parts =
                GitHubUrlParser.extractOwnerAndRepo(
                        repoUrl
                );

        String owner = parts[0];

        String repo = parts[1];

        String fileApiUrl =
                "https://api.github.com/repos/"
                        + owner
                        + "/"
                        + repo
                        + "/contents/"
                        + filePath;

        Map<String, Object> response =
                restTemplate.getForObject(
                        fileApiUrl,
                        Map.class
                );

        String encodedContent =
                String.valueOf(
                        response.get("content")
                );

        encodedContent =
                encodedContent.replace(
                        "\n",
                        ""
                );

        byte[] decodedBytes =
                Base64.getDecoder()
                        .decode(
                                encodedContent
                        );

        return new String(
                decodedBytes,
                StandardCharsets.UTF_8
        );

    } catch (Exception error) {

        return "Unable to load file content.";
    }
}

public String explainFile(
        String repoUrl,
        String filePath
) {

    String fileContent =
            getFileContent(
                    repoUrl,
                    filePath
            );

    String shortenedContent =
            fileContent.length() > 4000
                    ? fileContent.substring(
                    0,
                    4000
            )
                    : fileContent;

    String prompt =
            """
            Explain this code file in a beginner-friendly way.

            Explain:
            1. What this file does
            2. Main functionality
            3. Important classes/functions
            4. Technologies used

            Keep answer clean and concise.

            FILE PATH:
            %s

            CODE:
            %s
            """
                    .formatted(
                            filePath,
                            shortenedContent
                    );

    return ollamaService.generateChatResponse(
            prompt
    );
}

    public String chatWithRepository(
            String repoUrl,
            String question,
            boolean repositoryMode
    ) {

        if (!repositoryMode) {

            return ollamaService.generateChatResponse(
                    question
            );
        }

        String repositoryContent =
                getRepositoryContent(repoUrl);

        String prompt =
                """
                Repository Content:
                %s

                User Question:
                %s

                Answer only using repository knowledge.
                Keep answer short and beginner friendly.
                """
                        .formatted(
                                repositoryContent,
                                question
                        );

        return ollamaService.generateChatResponse(
                prompt
        );
    }

    private String getRepositoryContent(
            String repoUrl
    ) {

        String[] parts =
                GitHubUrlParser.extractOwnerAndRepo(
                        repoUrl
                );

        String owner = parts[0];

        String repo = parts[1];

        String repoApiUrl =
                "https://api.github.com/repos/"
                        + owner
                        + "/"
                        + repo;

        Map repoResponse =
                restTemplate.getForObject(
                        repoApiUrl,
                        Map.class
                );

        String description =
                String.valueOf(
                        repoResponse.get("description")
                );

        String language =
                String.valueOf(
                        repoResponse.get("language")
                );

        String readmeApiUrl =
                "https://api.github.com/repos/"
                        + owner
                        + "/"
                        + repo
                        + "/readme";

        Map readmeResponse =
                restTemplate.getForObject(
                        readmeApiUrl,
                        Map.class
                );

        String encodedContent =
                String.valueOf(
                        readmeResponse.get("content")
                );

        encodedContent =
                encodedContent.replace("\n", "");

        byte[] decodedBytes =
                Base64.getDecoder()
                        .decode(encodedContent);

        String readmeContent =
                new String(
                        decodedBytes,
                        StandardCharsets.UTF_8
                );

        readmeContent =
                ReadmeCleaner.clean(
                        readmeContent
                );

        if (readmeContent.length() > 3000) {

            readmeContent =
                    readmeContent.substring(
                            0,
                            3000
                    );
        }

        return
                "Repository: " + repo
                        + "\nDescription: " + description
                        + "\nMain Language: " + language
                        + "\nREADME:\n"
                        + readmeContent;
    }
}