package org.example.shortenerservice.service;

import org.example.shortenerservice.dto.LinkClickedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class LinkClickProducer {

    private static final String TOPIC = "link-clicks";

    private final KafkaTemplate<String, LinkClickedEvent> kafkaTemplate;

    public LinkClickProducer(KafkaTemplate<String, LinkClickedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public CompletableFuture<SendResult<String, LinkClickedEvent>> sendLinkClickedEvent(LinkClickedEvent event) {
        return kafkaTemplate.send(TOPIC, event.getShortCode(), event);
    }
}
