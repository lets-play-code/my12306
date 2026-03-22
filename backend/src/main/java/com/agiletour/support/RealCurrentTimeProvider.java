package com.agiletour.support;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Real implementation of CurrentTimeProvider that returns the actual current time.
 */
@Component
public class RealCurrentTimeProvider implements CurrentTimeProvider {

    @Override
    public LocalDateTime now() {
        return LocalDateTime.now();
    }
}
