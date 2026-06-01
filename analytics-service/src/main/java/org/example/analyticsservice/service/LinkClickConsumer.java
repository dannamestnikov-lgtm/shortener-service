package org.example.analyticsservice.service;

import org.example.analyticsservice.dto.LinkClickedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class LinkClickConsumer {
    private final FailedEventService failedEventService;
    private final AnalyticsService analyticsService;

    public LinkClickConsumer(FailedEventService failedEventService,
                             AnalyticsService analyticsService) {
        this.failedEventService = failedEventService;
        this.analyticsService = analyticsService;
    }

    @KafkaListener(topics = "link-clicks", groupId = "analytics-service")
    public void consume(LinkClickedEvent event){
        try {
            analyticsService.recordClick(event);
            System.out.println("Received click event: " + event.getShortCode()
                    + " | " + event.getOriginalUrl()
                    + " | " + event.getClickedAt()
                    + " | " + event.getIp());
        } catch (Exception e) {
            failedEventService.saveFailedEvent(event);
        }
    }
}
