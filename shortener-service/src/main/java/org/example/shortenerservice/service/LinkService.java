package org.example.shortenerservice.service;

import org.example.shortenerservice.dto.CreateLinkRequest;
import org.example.shortenerservice.dto.LinkClickedEvent;
import org.example.shortenerservice.dto.LinkResponse;
import org.example.shortenerservice.dto.LinkStatsResponse;
import org.example.shortenerservice.entity.OutboxEvent;
import org.example.shortenerservice.entity.OutboxStatus;
import org.example.shortenerservice.exception.LinkNotFoundException;
import org.example.shortenerservice.repository.LinkRepository;
import org.example.shortenerservice.repository.OutboxEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.example.shortenerservice.entity.Link;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@Service
public class LinkService {
    private LinkRepository linkRepository;
    private OutboxEventRepository outboxEventRepository;
    private LinkCacheService linkCacheService;
    private LinkClickProducer linkClickProducer;
    private static final Logger log = LoggerFactory.getLogger(LinkService.class);
    private final Counter linksCreatedCounter;
    private final Counter linksClicksCounter;
    @Value("${app.base-url:http://localhost:8080}")
    private String appBaseUrl;
    private final ObjectMapper objectMapper;

    public LinkService(LinkRepository linkRepository,
                       LinkCacheService linkCacheService,
                       LinkClickProducer linkClickProducer,
                       MeterRegistry meterRegistry,
                       OutboxEventRepository outboxEventRepository, ObjectMapper objectMapper){
        this.linkRepository = linkRepository;
        this.linkCacheService = linkCacheService;
        this.linkClickProducer = linkClickProducer;
        this.outboxEventRepository = outboxEventRepository;
        this.objectMapper = objectMapper;

        this.linksCreatedCounter = Counter.builder("links.creation.count")
                .description("Total number of created links")
                .register(meterRegistry);

        this.linksClicksCounter = Counter.builder("links.redirect.count")
                .description("Total number of link redirects")
                .register(meterRegistry);
    }

    public LinkResponse createLink(CreateLinkRequest request){
        Link link = new Link();
        link.setOriginalUrl(request.getOriginalUrl());
        link.setShortCode(generateCode());
        link.setClicks(0L);
        Link savedLink = linkRepository.save(link);
        linksCreatedCounter.increment();
        String shortUrl = appBaseUrl + "/"  + savedLink.getShortCode();
        LinkResponse response = new LinkResponse(savedLink.getShortCode(),
                                                 savedLink.getOriginalUrl(),
                                                 shortUrl,
                                                 savedLink.getClicks());
        return response;
    }

    private String generateCode(){
        return UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 8);
    }

    @Transactional
    public String redirect(String shortCode, String ip){
        String originalUrl = linkCacheService.getOriginalUrlByShortCode(shortCode);
         Link link = linkRepository.findByShortCode(shortCode)
         .orElseThrow(() -> new LinkNotFoundException("Link not found"));
         Long links = link.getClicks();
         links ++;
         link.setClicks(links);
         linkRepository.save(link);
         linksClicksCounter.increment();

        log.info("Redirect link: shortCode={}, originalUrl={}",
                link.getShortCode(),
                originalUrl);

        LocalDateTime clickedAt = LocalDateTime.now();

        LinkClickedEvent event = new LinkClickedEvent(
                link.getShortCode(),
                link.getOriginalUrl(),
                clickedAt,
                ip,
                MDC.get("CorrelationId")
        );

        String payload;

        try {
            payload = objectMapper.writeValueAsString(event);
        } catch (Exception e){
            throw new RuntimeException("Failed to serialize LinkClickedEvent", e);
        }

        OutboxEvent outboxEvent = new OutboxEvent(UUID.randomUUID(),
                "LINK",
                link.getShortCode(),
                "LINK_CLICKED",
                payload,
                OutboxStatus.PENDING,
                LocalDateTime.now());

        outboxEventRepository.save(outboxEvent);


         return originalUrl;
        }

        public LinkResponse getByShortCode(String shortCode){
        Link link = linkRepository.findByShortCode(shortCode)
        .orElseThrow(() -> new LinkNotFoundException("Link not found"));
        String shortUrl = appBaseUrl + "/"  + link.getShortCode();
        LinkResponse response = new LinkResponse(link.getShortCode(),
                link.getOriginalUrl(),
                shortUrl,
                link.getClicks());
        return response;
        }

        @CacheEvict(value = "links", key = "#shortCode")
        public void deleteByShortCode(String shortCode){
        Link link = linkRepository.findByShortCode(shortCode)
        .orElseThrow(() -> new LinkNotFoundException(shortCode));
        linkRepository.delete(link);
        }

        public LinkStatsResponse getStats(String shortCode){
        Link link = linkRepository.findByShortCode(shortCode)
        .orElseThrow(() -> new LinkNotFoundException(shortCode));
        LinkStatsResponse linkStatsResponse = new LinkStatsResponse(link.getShortCode(), link.getClicks());
        return linkStatsResponse;
        }
    }

