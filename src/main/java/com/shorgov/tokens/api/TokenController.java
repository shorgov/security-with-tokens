package com.shorgov.tokens.api;

import com.shorgov.tokens.model.RefreshTokenInput;
import com.shorgov.tokens.model.TokenData;
import com.shorgov.tokens.util.TokenManager;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class TokenController {

    private final TokenManager tokenManager;

    @PostMapping("/token/refresh")
    public ResponseEntity<TokenData> refreshToken(HttpServletRequest request, @RequestBody RefreshTokenInput rti) {

        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        String accessToken = authorization.substring("Bearer ".length());

        return ResponseEntity.ok(tokenManager.refreshTokens(accessToken, rti.refreshToken()));
    }

}
