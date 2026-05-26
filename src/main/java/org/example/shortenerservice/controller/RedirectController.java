package org.example.shortenerservice.controller;

import org.example.shortenerservice.service.LinkService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RedirectController {
    private LinkService linkService;
    public RedirectController(LinkService linkService) {
        this.linkService = linkService;
    }
    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirect(@PathVariable String shortCode){
        String originalUrl = linkService.redirect(shortCode);
        return ResponseEntity.status(HttpStatus.FOUND).header("Location", originalUrl).build();
    }
}
