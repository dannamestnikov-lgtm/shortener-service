package org.example.shortenerservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RateLimitServiceTest {

    private StringRedisTemplate stringRedisTemplate;
    private ValueOperations<String, String> valueOperations;
    private RateLimitService rateLimitService;

    private static final String IP = "127.0.0.1";
    private static final String KEY = "rate_limit:create_link:" + IP;

    @BeforeEach
    void setUp() {
        stringRedisTemplate = mock(StringRedisTemplate.class);
        valueOperations = mock(ValueOperations.class);

        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);

        rateLimitService = new RateLimitService(stringRedisTemplate);
    }

    @Test
    void firstRequestShouldNotBeBlocked() {
        when(valueOperations.increment(KEY)).thenReturn(1L);

        boolean result = rateLimitService.isRateLimited(IP);

        assertFalse(result);
    }

    @Test
    void eleventhRequestShouldBeBlocked() {
        when(valueOperations.increment(KEY)).thenReturn(11L);

        boolean result = rateLimitService.isRateLimited(IP);

        assertTrue(result);
    }

    @Test
    void firstRequestShouldSetTtl() {
        when(valueOperations.increment(KEY)).thenReturn(1L);

        rateLimitService.isRateLimited(IP);

        verify(stringRedisTemplate).expire(KEY, Duration.ofSeconds(60));
    }

    @Test
    void nextRequestsShouldNotResetTtl() {
        when(valueOperations.increment(KEY)).thenReturn(2L);

        rateLimitService.isRateLimited(IP);

        verify(stringRedisTemplate, never()).expire(anyString(), any(Duration.class));
    }
}
