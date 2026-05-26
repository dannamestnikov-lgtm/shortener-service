package org.example.shortenerservice.service;

import org.example.shortenerservice.dto.CreateLinkRequest;
import org.example.shortenerservice.dto.LinkResponse;
import org.example.shortenerservice.entity.Link;
import org.example.shortenerservice.exception.LinkNotFoundException;
import org.example.shortenerservice.repository.LinkRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LinkServiceTest {

    @Mock
    private LinkRepository linkRepository;

    @InjectMocks
    private LinkService linkService;

    @Test
    void createLink_savesAndReturnsResponse() {
        CreateLinkRequest request = new CreateLinkRequest();
        request.setOriginalUrl("https://google.com");

        Link savedLink = new Link();
        savedLink.setShortCode("abc12345");
        savedLink.setOriginalUrl("https://google.com");
        savedLink.setClicks(0L);
        when(linkRepository.save(any())).thenReturn(savedLink);
        LinkResponse response = linkService.createLink(request);

        assertEquals("abc12345", response.getShortCode());
        assertEquals("https://google.com", response.getOriginalUrl());
        assertEquals(0L, response.getClicks());
    }

    @Test
    void getByShortCode_whenExists_returnsLink() {
        Link savedLink = new Link();
        savedLink.setShortCode("abc12345");
        savedLink.setOriginalUrl("https://google.com");
        savedLink.setClicks(0L);
        when(linkRepository.findByShortCode("abc12345")).thenReturn(Optional.of(savedLink));
        LinkResponse response = linkService.getByShortCode(savedLink.getShortCode());

        assertEquals("abc12345", response.getShortCode());
        assertEquals("https://google.com", response.getOriginalUrl());
        assertEquals(0L, response.getClicks());

    }
        @Test
        void getByShortCode_whenNotExists_throwsException(){
            when(linkRepository.findByShortCode("notexist")).thenReturn(Optional.empty());
            assertThrows(LinkNotFoundException.class,
                    () -> linkService.getByShortCode("notexist"));
        }
    }

