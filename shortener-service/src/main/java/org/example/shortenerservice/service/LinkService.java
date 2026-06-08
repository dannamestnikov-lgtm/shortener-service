package org.example.shortenerservice.service;

import org.example.shortenerservice.dto.CreateLinkRequest;
import org.example.shortenerservice.dto.LinkClickedEvent;
import org.example.shortenerservice.dto.LinkResponse;
import org.example.shortenerservice.dto.LinkStatsResponse;
import org.example.shortenerservice.exception.LinkNotFoundException;
import org.example.shortenerservice.repository.LinkRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.example.shortenerservice.entity.Link;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class LinkService {
    private LinkRepository linkRepository;
    private LinkCacheService linkCacheService;
    private LinkClickProducer linkClickProducer;
    private static final Logger log = LoggerFactory.getLogger(LinkService.class);
    private final Counter linksCreatedCounter;
    private final Counter linksClicksCounter;
    @Value("${app.base-url:http://localhost:8080}")
    private String appBaseUrl;

    public LinkService(LinkRepository linkRepository,
                       LinkCacheService linkCacheService,
                       LinkClickProducer linkClickProducer,
                       MeterRegistry meterRegistry){
        this.linkRepository = linkRepository;
        this.linkCacheService = linkCacheService;
        this.linkClickProducer = linkClickProducer;

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
         LinkClickedEvent event = new LinkClickedEvent(link.getShortCode(),
                 originalUrl,
                 LocalDateTime.now(),
                 ip, MDC.get("correlationId"));

         linkClickProducer.sendLinkClickedEvent(event);

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

