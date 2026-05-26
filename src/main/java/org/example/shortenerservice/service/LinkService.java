package org.example.shortenerservice.service;

import org.example.shortenerservice.dto.CreateLinkRequest;
import org.example.shortenerservice.dto.LinkResponse;
import org.example.shortenerservice.dto.LinkStatsResponse;
import org.example.shortenerservice.exception.LinkNotFoundException;
import org.example.shortenerservice.repository.LinkRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.example.shortenerservice.entity.Link;

import java.util.UUID;

@Service
public class LinkService {
    private LinkRepository linkRepository;
    private LinkCacheService linkCacheService;

    public LinkService(LinkRepository linkRepository, LinkCacheService linkCacheService){
        this.linkRepository = linkRepository;
        this.linkCacheService = linkCacheService;
    }

    public LinkResponse createLink(CreateLinkRequest request){
        Link link = new Link();
        link.setOriginalUrl(request.getOriginalUrl());
        link.setShortCode(generateCode());
        link.setClicks(0L);
        Link savedLink = linkRepository.save(link);
        String shortUrl = "http://localhost:8080/" + savedLink.getShortCode();
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

    public String redirect(String shortCode){
        String originalUrl = linkCacheService.getOriginalUrlByShortCode(shortCode);
         Link link = linkRepository.findByShortCode(shortCode)
         .orElseThrow(() -> new LinkNotFoundException("Link not found"));
         Long links = link.getClicks();
         links ++;
         link.setClicks(links);
         linkRepository.save(link);
         return originalUrl;
        }

        public LinkResponse getByShortCode(String shortCode){
        Link link = linkRepository.findByShortCode(shortCode)
        .orElseThrow(() -> new LinkNotFoundException("Link not found"));
        String shortUrl = "http://localhost:8080/" + link.getShortCode();
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

