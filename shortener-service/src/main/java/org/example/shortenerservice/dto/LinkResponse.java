package org.example.shortenerservice.dto;

public class LinkResponse {
    private String shortCode;
    private String originalUrl;
    private String shortUrl;
    private Long clicks;

    public LinkResponse(String shortCode,
                        String originalUrl,
                        String shortUrl,
                        Long clicks){
        this.shortCode = shortCode;
        this.originalUrl = originalUrl;
        this.shortUrl = shortUrl;
        this.clicks = clicks;
    }

    public String getShortCode(){
        return shortCode;
    }

    public String getOriginalUrl(){
        return originalUrl;
    }

    public String getShortUrl(){
        return shortUrl;
    }

    public Long getClicks(){
        return clicks;
    }
}
