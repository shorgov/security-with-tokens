package com.shorgov.tokens.repo;

import com.shorgov.tokens.model.RefreshToken;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class RefreshTokenRepository {
    private static final ConcurrentHashMap<UUID, RefreshToken> refreshTokens = new ConcurrentHashMap<>();

    public Optional<RefreshToken> findByUUID(UUID uuid) {
        expunge();
        return Optional.ofNullable(refreshTokens.get(uuid));
    }

    public void save(UUID uuid, RefreshToken refreshToken) {
        expunge();
        refreshTokens.put(uuid, refreshToken);
    }

    public void remove(UUID uuid) {
        refreshTokens.remove(uuid);
        expunge();
    }

    //Mimic expiration - call before every action in order tp clear the expired tokens
    //in general this should be done in time expiring cache like Redis
    private void expunge() {
        refreshTokens.entrySet()
                .stream()
                .filter(e -> e.getValue().expirationTime() < System.currentTimeMillis())
                .forEach(e -> refreshTokens.remove(e.getKey()));
    }
}
