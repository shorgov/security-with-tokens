package com.shorgov.tokens.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shorgov.tokens.model.TokenData;
import com.shorgov.tokens.util.TokenManager;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

//alternatively it can be done in login controller with json input
//the controller can have authManager and on successful authentication by authProvider it will return the tokens
//this example shows how to use a filter instead
//see config for more details about filter registration
@RequiredArgsConstructor
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final TokenManager tokenManager;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        String username = request.getParameter("email");
        String password = request.getParameter("password");
        return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException {
        User user = (User) authResult.getPrincipal();

        TokenData tokenData = tokenManager.generateTokenPair(user.getUsername(),
                user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList());

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), tokenData);
    }
}
