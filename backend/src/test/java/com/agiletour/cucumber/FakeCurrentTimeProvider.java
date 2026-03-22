package com.agiletour.cucumber;

import com.agiletour.support.CurrentTimeProvider;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Fake implementation of CurrentTimeProvider for testing.
 * Time can be frozen to a specific value.
 */
@Component
public class FakeCurrentTimeProvider implements CurrentTimeProvider {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    
    private LocalDateTime frozenTime;

    public void setTime(String isoDateTime) {
        this.frozenTime = LocalDateTime.parse(isoDateTime, FORMATTER);
    }

    public void reset() {
        this.frozenTime = null;
    }

    @Override
    public LocalDateTime now() {
        if (frozenTime == null) {
            return LocalDateTime.now();
        }
        return frozenTime;
    }
}
