package com.example.repoexplainer.controller;

import com.example.repoexplainer.dto.ChatRequest;
import com.example.repoexplainer.dto.ChatResponse;
import com.example.repoexplainer.dto.RepoAnalyzeRequest;
import com.example.repoexplainer.dto.RepoAnalyzeResponse;
import com.example.repoexplainer.service.GitHubService;
import com.example.repoexplainer.util.AIResponseParser;

import org.springframework.web.bind.annotation.*;

import java.util.Map;

import com.example.repoexplainer.dto.FileNode;

import java.util.List;

import com.example.repoexplainer.dto.FileContentRequest;
import com.example.repoexplainer.service.EmbeddingService;
import com.example.repoexplainer.service.RagService;

import com.example.repoexplainer.dto.EmbeddingChunk;

@RestController
@RequestMapping("/api/repo")
@CrossOrigin(origins = "http://localhost:5173")
public class RepoController {

    private final GitHubService gitHubService;
    
    private final RagService ragService;

    public RepoController(

        GitHubService gitHubService,

        
        RagService ragService
) {

    this.gitHubService =
            gitHubService;

    

    this.ragService =
            ragService;
}

    @GetMapping("/health")
    public String health() {

        return "Backend Running Successfully";
    }

    @PostMapping("/analyze")
    public RepoAnalyzeResponse analyzeRepository(
            @RequestBody RepoAnalyzeRequest request
    ) {

        String aiResponse =
                gitHubService.getRepositoryInfo(
                        request.getRepoUrl()
                );

        Map<String, String> parsed =
                AIResponseParser.parse(
                        aiResponse
                );

        return RepoAnalyzeResponse.builder()
                .summary(parsed.get("summary"))
                .techStack(parsed.get("techStack"))
                .architecture(parsed.get("architecture"))
                .setupInstructions(parsed.get("setup"))
                .beginnerExplanation(
                        parsed.get(
                                "beginnerExplanation"
                        )
                )
                .build();
    }

    @PostMapping("/files")
        public List<FileNode> getRepositoryFiles(
                @RequestBody RepoAnalyzeRequest request
        ) {

        return gitHubService.getRepositoryFiles(
                request.getRepoUrl()
        );
        }

    @PostMapping("/file-content")
        public String getFileContent(
                @RequestBody FileContentRequest request
        ) {

        return gitHubService.getFileContent(
                request.getRepoUrl(),
                request.getFilePath()
        );
        }    

    @PostMapping("/chat")
public String chatWithRepository(

        @RequestBody ChatRequest request
) {

    if (!request.isRepositoryMode()) {

        return gitHubService.chatWithRepository(
                request.getRepoUrl(),
                request.getQuestion(),
                false
        );
    }

    return ragService.chatWithRepository(
            request.getQuestion()
    );
}

    

    @PostMapping("/explain-file")
public String explainFile(
        @RequestBody FileContentRequest request
) {

    return gitHubService.explainFile(
            request.getRepoUrl(),
            request.getFilePath()
    );
}

@PostMapping("/process-repository")
public String processRepository(

        @RequestBody RepoAnalyzeRequest request
) {

    ragService.processRepository(
            request.getRepoUrl()
    );

    return "Chunks created: "
            + ragService.getChunkCount();
}

@PostMapping("/search")
public List<EmbeddingChunk> search(

        @RequestBody ChatRequest request
) {

    return ragService.searchRelevantChunks(
            request.getQuestion()
    );
}
}