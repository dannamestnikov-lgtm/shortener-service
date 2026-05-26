package org.example.shortenerservice.dto;

public class LinkStatsResponse {
    private String shortCode;
    private Long clicks;

    public LinkStatsResponse(String shortCode, Long clicks) {
        this.shortCode = shortCode;
        this.clicks = clicks;
    }

    public String getShortCode() {
        return shortCode;
    }

    public Long getClicks() {
        return clicks;
    }
}
