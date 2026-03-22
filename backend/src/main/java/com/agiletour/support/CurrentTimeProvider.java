package com.agiletour.support;

import java.time.LocalDateTime;

/**
 * Provides the current time for the application.
 * Allows for time to be controlled/frozen during testing.
 */
public interface CurrentTimeProvider {

    /**
     * Returns the current date time.
     */
    LocalDateTime now();
}
