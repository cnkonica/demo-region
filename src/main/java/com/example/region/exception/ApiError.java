package com.example.region.exception;

import java.time.Instant;

public record ApiError(Instant timestamp, int status, String error, String path, String message) {
    // compact canonical constructor: set default when null
    public ApiError {
        if (timestamp == null) {
            timestamp = Instant.now();
        }
    }

    // convenience constructor that omits timestamp
    public ApiError(int status, String error, String path, String message) {
        this(Instant.now(), status, error, path, message);
    }
}