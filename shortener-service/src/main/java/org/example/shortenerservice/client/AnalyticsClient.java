package org.example.shortenerservice.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class AnalyticsClient {
    private final WebClient webClient;

    @Autowired
    public AnalyticsClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl("http://host.docker.internal:8081")
                .build();
    }

    public AnalyticsClient(WebClient.Builder webClientBuilder, String baseUrl) {
        this.webClient = webClientBuilder
                .baseUrl(baseUrl)
                .build();
    }

    public Integer getClicksByShortCode(String shortCode) {
        try {
            return webClient.get()
                    .uri("/api/analytics/{shortCode}", shortCode)
                    .retrieve()
                    .bodyToMono(Integer.class)
                    .block();
        } catch (Exception e) {
            return 0;
        }
    }
}
