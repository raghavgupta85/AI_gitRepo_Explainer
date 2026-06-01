package com.example.repoexplainer.service;

import com.example.repoexplainer.dto.EmbeddingChunk;
import com.example.repoexplainer.dto.FileNode;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RagService {

    private final GitHubService gitHubService;

    private final EmbeddingService embeddingService;

    private final OllamaService ollamaService;

    private final List<EmbeddingChunk> chunkStore =
            new ArrayList<>();

    private int processedFilesCount = 0;

    public RagService(

        GitHubService gitHubService,

        EmbeddingService embeddingService,

        OllamaService ollamaService
) {

    this.gitHubService =
            gitHubService;

    this.embeddingService =
            embeddingService;

    this.ollamaService =
            ollamaService;
}

    public void processRepository(
            String repoUrl
    ) {

        chunkStore.clear();

        processedFilesCount = 0;

        List<FileNode> files =
                gitHubService.getRepositoryFiles(
                        repoUrl
                );

        processNodes(
                repoUrl,
                files
        );
    }

    private void processNodes(

            String repoUrl,

            List<FileNode> nodes
    ) {

        for (FileNode node : nodes) {

            if (processedFilesCount >= 150) {
                return;
            }

            if (
                    node.getType()
                            .equals("file")
            ) {

                if (
                        shouldProcessFile(
                                node.getPath()
                        )
                ) {

                    processFile(
                            repoUrl,
                            node.getPath()
                    );

                    processedFilesCount++;
                }
            }

            if (
                    node.getChildren() != null
                            &&
                            !node.getChildren()
                                    .isEmpty()
            ) {

                processNodes(
                        repoUrl,
                        node.getChildren()
                );
            }
        }
    }

    private boolean shouldProcessFile(
            String path
    ) {

        String lowerPath =
                path.toLowerCase();

        if (
                lowerPath.contains("node_modules")
                        ||
                        lowerPath.contains("target")
                        ||
                        lowerPath.contains("build")
                        ||
                        lowerPath.contains(".git")
                        ||
                        lowerPath.contains("dist")
                        ||
                        lowerPath.contains(".idea")
        ) {

            return false;
        }

        return
                lowerPath.endsWith(".java")
                        ||
                        lowerPath.endsWith(".js")
                        ||
                        lowerPath.endsWith(".jsx")
                        ||
                        lowerPath.endsWith(".ts")
                        ||
                        lowerPath.endsWith(".tsx")
                        ||
                        lowerPath.endsWith(".py")
                        ||
                        lowerPath.endsWith(".md")
                        ||
                        lowerPath.endsWith(".json")
                        ||
                        lowerPath.endsWith(".xml")
                        ||
                        lowerPath.endsWith(".yml")
                        ||
                        lowerPath.endsWith(".properties");
    }

    private void processFile(

            String repoUrl,

            String filePath
    ) {

        try {

            String content =
                    gitHubService.getFileContent(
                            repoUrl,
                            filePath
                    );

            if (
                    content == null
                            ||
                            content.isBlank()
                            ||
                            content.length() > 5000
            ) {

                return;
            }

            List<String> chunks =
                    splitIntoChunks(content);

            for (String chunk : chunks) {

                List<Double> embedding =
                        embeddingService
                                .generateEmbedding(
                                        chunk
                                );

                EmbeddingChunk embeddingChunk =
                        EmbeddingChunk.builder()

                                .filePath(filePath)

                                .content(chunk)

                                .embedding(embedding)

                                .build();

                chunkStore.add(
                        embeddingChunk
                );
            }

        } catch (Exception error) {

            System.out.println(
                    "Failed to process: "
                            + filePath
            );
        }
    }

    private List<String> splitIntoChunks(
            String text
    ) {

        List<String> chunks =
                new ArrayList<>();

        int chunkSize = 1200;

        for (
                int i = 0;
                i < text.length();
                i += chunkSize
        ) {

            int end =
                    Math.min(
                            text.length(),
                            i + chunkSize
                    );

            chunks.add(
                    text.substring(i, end)
            );
        }

        return chunks;
    }

    public int getChunkCount() {

        return chunkStore.size();
    }

    private double cosineSimilarity(

            List<Double> vectorA,

            List<Double> vectorB
    ) {

        double dotProduct = 0.0;

        double normA = 0.0;

        double normB = 0.0;

        for (
                int i = 0;
                i < vectorA.size();
                i++
        ) {

            dotProduct +=
                    vectorA.get(i)
                            *
                            vectorB.get(i);

            normA +=
                    Math.pow(
                            vectorA.get(i),
                            2
                    );

            normB +=
                    Math.pow(
                            vectorB.get(i),
                            2
                    );
        }

        return dotProduct
                /
                (
                        Math.sqrt(normA)
                                *
                                Math.sqrt(normB)
                );
    }

    public List<EmbeddingChunk> searchRelevantChunks(

            String question
    ) {

        List<Double> questionEmbedding =
                embeddingService.generateEmbedding(
                        question
                );

        return chunkStore.stream()

                .sorted((a, b) -> {

                    double similarityA =
                            cosineSimilarity(
                                    questionEmbedding,
                                    a.getEmbedding()
                            );

                    double similarityB =
                            cosineSimilarity(
                                    questionEmbedding,
                                    b.getEmbedding()
                            );

                    return Double.compare(
                            similarityB,
                            similarityA
                    );
                })

                .limit(8)

                .toList();
    }
    public String chatWithRepository(

        String question
) {

    List<EmbeddingChunk> relevantChunks =
            searchRelevantChunks(
                    question
            );

    System.out.println(
            "\n===== RAG SEARCH ====="
    );

    System.out.println(
            "Question: "
                    + question
    );

    System.out.println(
            "Relevant Chunks Found: "
                    + relevantChunks.size()
    );

    StringBuilder context =
            new StringBuilder();

    for (EmbeddingChunk chunk : relevantChunks) {

        System.out.println(
        "\nUsing File: "
                + chunk.getFilePath()
);

System.out.println(
        "Chunk Preview:\n"
                + chunk.getContent()
                        .substring(
                                0,
                                Math.min(
                                        300,
                                        chunk.getContent().length()
                                )
                        )
);

        context.append(
                "\nFILE: "
        );

        context.append(
                chunk.getFilePath()
        );

        context.append(
                "\nCONTENT:\n"
        );

        context.append(
                chunk.getContent()
        );

        context.append(
                "\n-------------------\n"
        );
    }

    String prompt =
            """
            You are an expert repository AI assistant.

            STRICT RULES:
            1. Answer ONLY from repository context.
            2. Mention exact file names when possible.
            3. Mention exact package names when possible.
            4. If information is missing, say:
               "I could not find that information in the repository."
            5. Do NOT give generic programming explanations.

            REPOSITORY CONTEXT:
            %s

            USER QUESTION:
            %s
            """
                    .formatted(
                            context.toString(),
                            question
                    );

    return ollamaService
            .generateChatResponse(
                    prompt
            );
}
}