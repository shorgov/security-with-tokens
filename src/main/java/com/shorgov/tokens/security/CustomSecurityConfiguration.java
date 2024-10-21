package com.shorgov.tokens.security;

import com.shorgov.tokens.filters.CustomAuthenticationFilter;
import com.shorgov.tokens.filters.CustomAuthorizationFilter;
import com.shorgov.tokens.util.TokenManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class CustomSecurityConfiguration {
    @Bean
    public AuthenticationProvider getAuthProvider(UserDetailsService userService, PasswordEncoder encoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userService);
        provider.setPasswordEncoder(encoder);
        return provider;
    }

    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager getAuthManager(HttpSecurity http, List<AuthenticationProvider> providers) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        if (authenticationManagerBuilder != null) {
            providers.forEach(authenticationManagerBuilder::authenticationProvider);
            return authenticationManagerBuilder.build();
        } else {
            return new ProviderManager(providers);
        }
    }

    @Bean
    SecurityFilterChain springWebFilterChain(HttpSecurity http, AuthenticationManager authManager, TokenManager tokenManager) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable);

        http.sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/error").permitAll()
                .requestMatchers("/api/login").permitAll()
                .requestMatchers("/api/token/refresh").permitAll()
                .requestMatchers("/api/data/free").permitAll()
                .requestMatchers("/api/data/admin").hasAuthority("ADMIN")
                .requestMatchers("/api/data/**").authenticated()
                .requestMatchers("/api/data").authenticated()
                .anyRequest().authenticated()
        );

        http.authenticationManager(authManager);

        CustomAuthenticationFilter authenticationFilter = new CustomAuthenticationFilter(authManager, tokenManager);
        authenticationFilter.setFilterProcessesUrl("/api/login");
        http.addFilter(authenticationFilter);
        CustomAuthorizationFilter authorizationFilter = new CustomAuthorizationFilter(tokenManager);
        http.addFilterBefore(authorizationFilter, CustomAuthenticationFilter.class);

        return http.build();
    }

}
