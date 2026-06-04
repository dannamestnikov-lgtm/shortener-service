package org.example.analyticsservice.dto;

import java.time.LocalDateTime;

public class LinkClickedEvent {
        private String shortCode;
        private String originalUrl;
        private LocalDateTime clickedAt;
        private String ip;
        private String correlationId;

        public LinkClickedEvent() {
        }

        public LinkClickedEvent(String shortCode,
                                String originalUrl,
                                LocalDateTime clickedAt,
                                String ip) {
            this.shortCode = shortCode;
            this.originalUrl = originalUrl;
            this.clickedAt = clickedAt;
            this.ip = ip;
    }

        public String getShortCode() {
            return shortCode;
        }

        public void setShortCode(String shortCode) {
            this.shortCode = shortCode;
        }

        public String getOriginalUrl() {
            return originalUrl;
        }

        public String getCorrelationId() {
        return correlationId;
        }

        public void setOriginalUrl(String originalUrl) {
            this.originalUrl = originalUrl;
        }

        public LocalDateTime getClickedAt() {
            return clickedAt;
        }

        public void setClickedAt(LocalDateTime clickedAt) {
            this.clickedAt = clickedAt;
        }

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
         }
    }

