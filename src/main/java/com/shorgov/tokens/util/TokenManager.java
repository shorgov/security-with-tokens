package com.shorgov.tokens.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.shorgov.tokens.model.RefreshToken;
import com.shorgov.tokens.model.TokenData;
import com.shorgov.tokens.repo.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TokenManager {

    //usually added as .env / vault not part of code repo
    private static final String SECRET_KEY = "secret_key_that_no_one_knows";
    private static final String ISSUER = "Tokens APP";

    @Value("${auth.tokens.accessTokenDurationInMin}")
    private int accessTokenDurationInMin;
    @Value("${auth.tokens.refreshTokenDurationInHours}")
    private int refreshTokenDurationInHours;

    private final RefreshTokenRepository refreshTokenRepository;

    public TokenData generateTokenPair(String subject, List<String> roles) {

        UUID uuid = UUID.randomUUID();

        String accessToken = JWT.create()
                .withJWTId(uuid.toString())
                .withSubject(subject)
                .withIssuer(ISSUER)
                .withExpiresAt(Instant.now().plus(accessTokenDurationInMin, ChronoUnit.MINUTES))
                .withClaim("roles", roles)
                .sign(TokenManager.getJWTAlgorithm());


        long refreshTokenExpirationTime = Instant.now().plus(refreshTokenDurationInHours, ChronoUnit.HOURS).toEpochMilli();
        RefreshToken refreshToken = new RefreshToken(uuid, subject, refreshTokenExpirationTime);
        String refreshTokenString = refreshToken.toTokenString();

        refreshTokenRepository.save(uuid, refreshToken);

        return new TokenData(accessToken, refreshTokenString);
    }

    public static Algorithm getJWTAlgorithm() {
        return Algorithm.HMAC256(TokenManager.SECRET_KEY);//or Algorithm.RSA256 with custom keystore
    }

    public void setupSecurityContext(String token) {
        JWTVerifier verifier = JWT.require(TokenManager.getJWTAlgorithm()).withIssuer(TokenManager.ISSUER).build();
        DecodedJWT jwt = verifier.verify(token);
        String[] roles = jwt.getClaim("roles").asArray(String.class);
        List<SimpleGrantedAuthority> authorities = roles == null ?
                Collections.emptyList() : Arrays.stream(roles).map(SimpleGrantedAuthority::new).toList();
        PreAuthenticatedAuthenticationToken auth = new PreAuthenticatedAuthenticationToken(jwt.getSubject(), null, authorities);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    public TokenData refreshTokens(String accessToken, String refreshTokenId) {
        //access token might be expired already so discard its validity, just decode it
        DecodedJWT accessTokenDecoded = JWT.decode(accessToken);
        List<String> roles = accessTokenDecoded.getClaim("roles").asList(String.class);
        UUID refreshTokenUUID = UUID.fromString(refreshTokenId);
        RefreshToken refreshToken = refreshTokenRepository.findByUUID(refreshTokenUUID)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token is not valid"));

        refreshTokenRepository.remove(refreshTokenUUID);

        if (!accessTokenDecoded.getId().equals(refreshTokenId) || !accessTokenDecoded.getSubject().equals(refreshToken.email())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token is not valid");
        }
        return generateTokenPair(accessTokenDecoded.getSubject(), roles);
    }
}
