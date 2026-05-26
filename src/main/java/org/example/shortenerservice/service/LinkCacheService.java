package org.example.shortenerservice.service;

import org.example.shortenerservice.entity.Link;
import org.example.shortenerservice.exception.LinkNotFoundException;
import org.example.shortenerservice.repository.LinkRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class LinkCacheService {
    private final LinkRepository linkRepository;
    public LinkCacheService(LinkRepository linkRepository) {
        this.linkRepository = linkRepository;
    }
    @Cacheable(value = "links", key = "#shortCode")
        public String getOriginalUrlByShortCode(String shortCode){
        Link link = linkRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new LinkNotFoundException(shortCode));
        return link.getOriginalUrl();
    }
}
