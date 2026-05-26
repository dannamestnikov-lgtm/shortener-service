package org.example.shortenerservice.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RateLimitService {
    private static final int REQUEST_LIMIT = 10;
    private static final Duration WINDOW = Duration.ofSeconds(60);
    private static final String KEY_PREFIX = "rate_limit:create_link:";
    private final StringRedisTemplate stringRedisTemplate;
    public RateLimitService(StringRedisTemplate stringRedisTemplate){
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public boolean isRateLimited(String ip){
        String key = KEY_PREFIX + ip;
        Long requestCount = stringRedisTemplate.opsForValue().increment(key);
        if (requestCount == 1) {
            stringRedisTemplate.expire(key, WINDOW);
        }
            return requestCount > REQUEST_LIMIT;
        }
    }

