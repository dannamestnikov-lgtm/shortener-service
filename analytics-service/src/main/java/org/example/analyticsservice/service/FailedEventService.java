package org.example.analyticsservice.service;

import org.example.analyticsservice.dto.LinkClickedEvent;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FailedEventService {
    private final List<LinkClickedEvent> failedEvents = new ArrayList<>();

    public void saveFailedEvent(LinkClickedEvent event){
        failedEvents.add(event);
    }

    public List<LinkClickedEvent> getFailedEvents(){
     return failedEvents;
    }
}
