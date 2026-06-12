package org.example.shortenerservice.outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.example.shortenerservice.dto.LinkClickedEvent;
import org.example.shortenerservice.entity.OutboxEvent;
import org.example.shortenerservice.entity.OutboxStatus;
import org.example.shortenerservice.repository.OutboxEventRepository;
import org.example.shortenerservice.service.LinkClickProducer;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.support.SendResult;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OutboxRelayTest {

    private final OutboxEventRepository outboxEventRepository = mock(OutboxEventRepository.class);
    private final LinkClickProducer linkClickProducer = mock(LinkClickProducer.class);
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Test
    void processOutbox_whenKafkaSuccess_marksEventAsSent() throws Exception {
        LinkClickedEvent linkClickedEvent = new LinkClickedEvent(
                "abc123",
                "https://google.com",
                LocalDateTime.now(),
                "127.0.0.1",
                "test-correlation-id"
        );

        String payload = objectMapper.writeValueAsString(linkClickedEvent);

        OutboxEvent outboxEvent = new OutboxEvent(
                UUID.randomUUID(),
                "LINK",
                "abc123",
                "LINK_CLICKED",
                payload,
                OutboxStatus.PENDING,
                LocalDateTime.now()
        );

        when(outboxEventRepository.findTop100ByStatusOrderByCreatedAtAsc(OutboxStatus.PENDING))
                .thenReturn(List.of(outboxEvent));

        CompletableFuture<SendResult<String, LinkClickedEvent>> successFuture =
                CompletableFuture.completedFuture(null);

        when(linkClickProducer.sendLinkClickedEvent(any(LinkClickedEvent.class)))
                .thenReturn(successFuture);

        OutboxRelay outboxRelay = new OutboxRelay(
                outboxEventRepository,
                linkClickProducer,
                objectMapper,
                new SimpleMeterRegistry()
        );

        outboxRelay.processOutbox();

        verify(linkClickProducer).sendLinkClickedEvent(any(LinkClickedEvent.class));
        verify(outboxEventRepository).save(outboxEvent);

        assertEquals(OutboxStatus.SENT, outboxEvent.getStatus());
    }

    @Test
    void processOutbox_whenKafkaFails_keepsEventPending() throws Exception {
        LinkClickedEvent linkClickedEvent = new LinkClickedEvent(
                "abc123",
                "https://google.com",
                LocalDateTime.now(),
                "127.0.0.1",
                "test-correlation-id"
        );

        String payload = objectMapper.writeValueAsString(linkClickedEvent);

        OutboxEvent outboxEvent = new OutboxEvent(
                UUID.randomUUID(),
                "LINK",
                "abc123",
                "LINK_CLICKED",
                payload,
                OutboxStatus.PENDING,
                LocalDateTime.now()
        );

        when(outboxEventRepository.findTop100ByStatusOrderByCreatedAtAsc(OutboxStatus.PENDING))
                .thenReturn(List.of(outboxEvent));

        CompletableFuture<SendResult<String, LinkClickedEvent>> failedFuture = new CompletableFuture<>();
        failedFuture.completeExceptionally(new RuntimeException("Kafka is unavailable"));

        when(linkClickProducer.sendLinkClickedEvent(any(LinkClickedEvent.class)))
                .thenReturn(failedFuture);

        OutboxRelay outboxRelay = new OutboxRelay(
                outboxEventRepository,
                linkClickProducer,
                objectMapper,
                new SimpleMeterRegistry()
        );

        outboxRelay.processOutbox();

        verify(linkClickProducer).sendLinkClickedEvent(any(LinkClickedEvent.class));
        verify(outboxEventRepository, never()).save(outboxEvent);

        assertEquals(OutboxStatus.PENDING, outboxEvent.getStatus());
    }
}
