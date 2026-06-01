package org.example.analyticsservice.service;

import org.example.analyticsservice.dto.LinkClickedEvent;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AnalyticsService {
    private final Map<String, Integer> clickStats = new HashMap<>();

    public void recordClick(LinkClickedEvent event){
        String shortCode = event.getShortCode();
        Integer currentClicks = clickStats.getOrDefault(shortCode, 0);
        clickStats.put(shortCode, currentClicks + 1);
    }

    public Integer getClicksByShortCode(String shortCode){
      return clickStats.getOrDefault(shortCode, 0);
    }
}
