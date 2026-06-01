package org.example.shortenerservice.dto;

import jakarta.validation.constraints.NotBlank;

public class CreateLinkRequest {
    @NotBlank
    private String originalUrl;

    public String getOriginalUrl(){
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl){
        this.originalUrl = originalUrl;
    }
}
