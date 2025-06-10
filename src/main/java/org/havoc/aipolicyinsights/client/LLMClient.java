package org.havoc.aipolicyinsights.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Slf4j
@Component
public class LLMClient {

    @Value("${huggingface.api.url}")
    private String apiUrl;

    @Value("${huggingface.api.token}")
    private String apiToken;

    private final WebClient webClient = WebClient.builder().build();

    public String askQuestion(String context, String question) {
        Map<String, String> payload = Map.of(
                "inputs", String.format("Context: %s\nQuestion: %s", context, question)
        );

        String result = webClient.post()
                .uri(apiUrl)
                .header("Authorization", "Bearer " + apiToken)
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        log.info("🤖 LLM raw response: {}", result);

        return result;
    }
}
