package org.example.shortenerservice.outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.example.shortenerservice.dto.LinkClickedEvent;
import org.example.shortenerservice.entity.OutboxEvent;
import org.example.shortenerservice.entity.OutboxStatus;
import org.example.shortenerservice.repository.OutboxEventRepository;
import org.example.shortenerservice.service.LinkClickProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OutboxRelay {

    private static final Logger log = LoggerFactory.getLogger(OutboxRelay.class);

    private final OutboxEventRepository outboxEventRepository;
    private final LinkClickProducer linkClickProducer;
    private final ObjectMapper objectMapper;

    public OutboxRelay(OutboxEventRepository outboxEventRepository,
                       LinkClickProducer linkClickProducer,
                       ObjectMapper objectMapper,
                       MeterRegistry meterRegistry) {
        this.outboxEventRepository = outboxEventRepository;
        this.linkClickProducer = linkClickProducer;
        this.objectMapper = objectMapper;

        Gauge.builder("outbox.pending.count",
                        outboxEventRepository,
                        repository -> repository.countByStatus(OutboxStatus.PENDING))
                .description("Number of pending outbox events")
                .register(meterRegistry);
    }

    @Scheduled(fixedDelay = 5000)
    public void processOutbox() {
        List<OutboxEvent> pendingEvents =
                outboxEventRepository.findTop100ByStatusOrderByCreatedAtAsc(OutboxStatus.PENDING);

        if (pendingEvents.isEmpty()) {
            return;
        }

        for (OutboxEvent outboxEvent : pendingEvents) {
            try {
                LinkClickedEvent event = objectMapper.readValue(
                        outboxEvent.getPayload(),
                        LinkClickedEvent.class
                );

                linkClickProducer.sendLinkClickedEvent(event).get();

                outboxEvent.markAsSent();
                outboxEventRepository.save(outboxEvent);
            } catch (Exception e) {
                log.error("Failed to process outbox event: id={}", outboxEvent.getId(), e);
            }
        }
    }
}



