package org.example.shortenerservice.service;

import org.example.shortenerservice.client.AnalyticsClient;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AnalyticsClientTest {
    @Test
    void getClicksByShortCode_whenAnalyticsUnavailable_returnsZero(){
        WebClient.Builder builder = WebClient.builder();
        AnalyticsClient analyticsClient = new AnalyticsClient(builder, "http://localhost:9999");
        Integer clicks = analyticsClient.getClicksByShortCode("abc123");
        assertEquals(0, clicks);
    }
}
