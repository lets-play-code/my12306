package com.agiletour.controller;

/**
 * Exception for unauthorized access (HTTP 401).
 */
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }
}
