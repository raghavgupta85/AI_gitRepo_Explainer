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

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import org.springframework.beans.factory.annotation.Value;

@Service
public class GitHubService {

    private final RestTemplate restTemplate;

    private final GroqService groqService;

    
    @Value("${github.token}")
private String githubToken;

    public GitHubService(

        RestTemplate restTemplate,

        GroqService groqService

        
) {

    this.restTemplate =
            restTemplate;

    this.groqService = groqService;

    
}

    public String getRepositoryInfo(
            String repoUrl
    ) {

        String repositoryContent =
                getRepositoryContent(repoUrl);

        return groqService.generateResponse(
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

    String defaultBranch =
            getDefaultBranch(
                    owner,
                    repo
            );

    String contentsApiUrl =
            "https://api.github.com/repos/"
                    + owner
                    + "/"
                    + repo
                    + "/git/trees/"
                    + defaultBranch
                    + "?recursive=1";

    HttpEntity<String> entity =
            new HttpEntity<>(
                    createHeaders()
            );

    ResponseEntity<Map> responseEntity =
            restTemplate.exchange(
                    contentsApiUrl,
                    HttpMethod.GET,
                    entity,
                    Map.class
            );

    Map<String, Object> response =
            responseEntity.getBody();

    if (response == null) {

        return new ArrayList<>();
    }

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

private String getDefaultBranch(

        String owner,

        String repo
) {

    try {

        String repoApiUrl =
                "https://api.github.com/repos/"
                        + owner
                        + "/"
                        + repo;

        HttpEntity<String> entity =
                new HttpEntity<>(
                        createHeaders()
                );

        ResponseEntity<Map> responseEntity =
                restTemplate.exchange(
                        repoApiUrl,
                        HttpMethod.GET,
                        entity,
                        Map.class
                );

        Map<String, Object> response =
                responseEntity.getBody();

        if (response == null) {

            return "master";
        }

        return response
                .get("default_branch")
                .toString();

    } catch (Exception error) {

        error.printStackTrace();

        return "master";
    }
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

        String defaultBranch =
                getDefaultBranch(
                        owner,
                        repo
                );

        String apiUrl =
                "https://raw.githubusercontent.com/"
                        + owner
                        + "/"
                        + repo
                        + "/"
                        + defaultBranch
                        + "/"
                        + filePath;

        HttpEntity<String> entity =
                new HttpEntity<>(
                        createHeaders()
                );

        ResponseEntity<String> response =
                restTemplate.exchange(
                        apiUrl,
                        HttpMethod.GET,
                        entity,
                        String.class
                );

        return response.getBody();

    } catch (Exception error) {

        error.printStackTrace();

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

    return groqService.generateResponse(
        prompt
);
}

    public String chatWithRepository(

        String repoUrl,

        String question,

        boolean repositoryMode
) {

    return groqService.generateResponse(
        question
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

    private HttpHeaders createHeaders() {

    HttpHeaders headers =
            new HttpHeaders();

    headers.set(
            "Authorization",
            "token " + githubToken
    );

    headers.set(
            "Accept",
            "application/vnd.github+json"
    );

    return headers;
}
}


