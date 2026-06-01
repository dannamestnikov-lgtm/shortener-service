package org.example.analyticsservice.controller;

import org.example.analyticsservice.dto.LinkClickedEvent;
import org.example.analyticsservice.service.AnalyticsService;
import org.example.analyticsservice.service.FailedEventService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {
    private final FailedEventService failedEventService;
    private final AnalyticsService analyticsService;

    public AnalyticsController(FailedEventService failedEventService,
                               AnalyticsService analyticsService){
        this.failedEventService = failedEventService;
        this.analyticsService = analyticsService;
    }

    @GetMapping("/failed")
    public List<LinkClickedEvent> getFailedEvents() {
        return failedEventService.getFailedEvents();
    }

    @GetMapping("/{shortCode}")
    public Integer getClicksByShortCode (@PathVariable String shortCode){
    Integer clicks = analyticsService.getClicksByShortCode(shortCode);
     return clicks;
    }
}
