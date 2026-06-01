package org.example.analyticsservice.service;

import org.example.analyticsservice.dto.LinkClickedEvent;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AnalyticsServiceTest {
    @Test
    void recordClick_increasesClicksToOne() {
        AnalyticsService analyticsService = new AnalyticsService();
        LinkClickedEvent event = new LinkClickedEvent("abc123",
                "https://vk.com",
                LocalDateTime.now(),
                "127.0.0.1");
        analyticsService.recordClick(event);
        Integer clicks = analyticsService.getClicksByShortCode("abc123");
        assertEquals(1, clicks);
    }

    @Test
    void recordClick_whenCalledTwice_increasesClicksToTwo(){
    AnalyticsService analyticsService = new AnalyticsService();
        LinkClickedEvent event = new LinkClickedEvent("abc123",
                "https://vk.com",
                LocalDateTime.now(),
                "127.0.0.1");
        analyticsService.recordClick(event);
        analyticsService.recordClick(event);
        Integer clicks = analyticsService.getClicksByShortCode("abc123");
        assertEquals(2, clicks);
    }
}
