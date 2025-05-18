package com.pap_shop.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.crypto.spec.SecretKeySpec;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Array of public endpoints that do not require authentication.
     */
    private final String[] PUBLIC_ENDPOINTS = {"/api/auth/login",
            "/api/auth/register"};

    /**
     * JWT secret key injected from application properties.
     */
    @Value("${jwt.secretkey}")
    private String secretkey;

    /**
     * Configures the security filter chain for HTTP requests.
     *
     * @param http the HttpSecurity object to configure
     * @return the configured SecurityFilterChain
     * @throws Exception if an error occurs during configuration
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);

        http.oauth2ResourceServer(oauth2 ->
                oauth2.jwt(jwtConfigurer -> jwtConfigurer.decoder(jwtDecoder())));

        http.authorizeHttpRequests(request -> {
            for (String endpoint : PUBLIC_ENDPOINTS) {
                request.requestMatchers(new AntPathRequestMatcher(endpoint, HttpMethod.POST.name())).permitAll();
            }
            request.anyRequest().authenticated();
        });
        return http.build();
    }

    /**
     * Creates a JWT decoder to validate JWT tokens using the HS256 algorithm.
     *
     * @return the configured JwtDecoder
     */
    @Bean
    JwtDecoder jwtDecoder(){
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretkey.getBytes(),"HS256");
        return NimbusJwtDecoder
                .withSecretKey(secretKeySpec)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();

    }
}