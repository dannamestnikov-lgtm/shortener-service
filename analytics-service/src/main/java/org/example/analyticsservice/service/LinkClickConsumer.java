package org.example.analyticsservice.service;

import org.example.analyticsservice.dto.LinkClickedEvent;
import org.slf4j.MDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class LinkClickConsumer {
    private final FailedEventService failedEventService;
    private final AnalyticsService analyticsService;
    private static final Logger log = LoggerFactory.getLogger(LinkClickConsumer.class);

    public LinkClickConsumer(FailedEventService failedEventService,
                             AnalyticsService analyticsService) {
        this.failedEventService = failedEventService;
        this.analyticsService = analyticsService;
    }

    @KafkaListener(topics = "link-clicks", groupId = "analytics-service")
    public void consume(LinkClickedEvent event){
        MDC.put("correlationId", event.getCorrelationId());
        try {
            analyticsService.recordClick(event);
            log.info("Received click event: shortCode={}, originalUrl={}, clickedAt={}, ip={}",
                    event.getShortCode(),
                    event.getOriginalUrl(),
                    event.getClickedAt(),
                    event.getIp());
        } catch (Exception e) {
            failedEventService.saveFailedEvent(event);
            log.error("Failed to process click event: shortCode={}", event.getShortCode(), e);
        } finally {
            MDC.clear();
        }
    }
}
