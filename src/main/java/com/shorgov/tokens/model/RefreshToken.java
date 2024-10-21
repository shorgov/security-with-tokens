package com.shorgov.tokens.model;

import java.util.UUID;

public record RefreshToken(UUID uuid, String email, long expirationTime) {
    public String toTokenString() {
        return uuid.toString();
    }
}
