package org.example.shortenerservice.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "links")
public class Link {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "short_code", unique = true, nullable = false)
    private String shortCode;

    @Column(name = "original_url", nullable = false)
    private String originalUrl;

    @Column
    private Long clicks = 0L;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    public void setOriginalUrl(String originalUrl){
        this.originalUrl = originalUrl;
    }

    public void setShortCode(String shortCode){
        this.shortCode = shortCode;
    }

    public void setClicks(Long clicks){
        this.clicks = clicks;
    }

    public String getShortCode() {
        return shortCode;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public Long getClicks() {
        return clicks;
    }
}
