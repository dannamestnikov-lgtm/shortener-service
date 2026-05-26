package org.example.shortenerservice.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.example.shortenerservice.dto.CreateLinkRequest;
import org.example.shortenerservice.dto.LinkResponse;
import org.example.shortenerservice.dto.LinkStatsResponse;
import org.example.shortenerservice.service.LinkService;
import org.example.shortenerservice.service.RateLimitService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/links")
public class LinkController {
private LinkService linkService;
private RateLimitService rateLimitService;
private HttpServletRequest httpRequest;

public LinkController(LinkService linkService, RateLimitService rateLimitService){
    this.linkService = linkService;
    this.rateLimitService = rateLimitService;   
}

@PostMapping
public ResponseEntity<LinkResponse> createLink(@Valid @RequestBody CreateLinkRequest request,
                                               HttpServletRequest httpRequest){
    String ip = httpRequest.getRemoteAddr();

    if (rateLimitService.isRateLimited(ip)){
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
    }

    LinkResponse response = linkService.createLink(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
}

@GetMapping("/{shortCode}")
    public ResponseEntity<LinkResponse> getByShortCode(@PathVariable String shortCode){
    LinkResponse response = linkService.getByShortCode(shortCode);
    return ResponseEntity.status(HttpStatus.OK).body(response);
}

@DeleteMapping("/{shortCode}")
    public ResponseEntity<Void> deleteByShortCode(@PathVariable String shortCode){
    linkService.deleteByShortCode(shortCode);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
}

@GetMapping("/{shortCode}/stats")
    public ResponseEntity<LinkStatsResponse> getStats(@PathVariable String shortCode){
    LinkStatsResponse response = linkService.getStats(shortCode);
    return ResponseEntity.status(HttpStatus.OK).body(response);
}
}
