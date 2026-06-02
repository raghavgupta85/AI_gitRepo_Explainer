// package com.example.repoexplainer.service;

// import org.springframework.http.*;
// import org.springframework.stereotype.Service;
// import org.springframework.web.client.RestTemplate;

// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;

// @Service
// public class EmbeddingService {

//     private final RestTemplate restTemplate;

//     public EmbeddingService(
//             RestTemplate restTemplate
//     ) {

//         this.restTemplate = restTemplate;
//     }

//     public List<Double> generateEmbedding(
//             String text
//     ) {

//         try {

//             String ollamaApi =
//                     "http://localhost:11434/api/embeddings";

//             Map<String, Object> request =
//                     new HashMap<>();

//             request.put(
//                     "model",
//                     "nomic-embed-text"
//             );

//             request.put(
//                     "prompt",
//                     text
//             );

//             HttpHeaders headers =
//                     new HttpHeaders();

//             headers.setContentType(
//                     MediaType.APPLICATION_JSON
//             );

//             HttpEntity<Map<String, Object>> entity =
//                     new HttpEntity<>(
//                             request,
//                             headers
//                     );

//             ResponseEntity<Map> response =
//                     restTemplate.postForEntity(
//                             ollamaApi,
//                             entity,
//                             Map.class
//                     );

//             Map body =
//                     response.getBody();

//             return (List<Double>)
//                     body.get("embedding");

//         } catch (Exception error) {

//             error.printStackTrace();

//             return List.of();
//         }
//     }
// }