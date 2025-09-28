package com.pap_shop.configuration;

import com.pap_shop.util.CustomJwtAuthenticationConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

import javax.crypto.spec.SecretKeySpec;
import java.util.Map;
import java.util.HashMap;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Array of public endpoints that do not require authentication.
     */
    private final Map<String, HttpMethod> PUBLIC_ENDPOINTS = new HashMap<String, HttpMethod>() {{
        put("/api/user/login", HttpMethod.POST);
        put("/api/user/register", HttpMethod.POST);
        put("/api/user/forgot-password", HttpMethod.POST);
        put("/api/user/validate-reset-code", HttpMethod.POST);
        put("/api/user/reset-password", HttpMethod.PUT);
        put("/api/role", HttpMethod.GET);
        put("/api/products", HttpMethod.GET);
        put("/api/products/search", HttpMethod.GET);
        put("/api/category", HttpMethod.GET);
        put("/api/user/logout", HttpMethod.POST);
        put("/api/products/*/images", HttpMethod.GET);
    }};

    private final Map<String, HttpMethod> ADMIN_ENDPOINTS = new HashMap<String, HttpMethod>() {{
        put("/api/role", HttpMethod.POST);
        put("/api/category", HttpMethod.POST);
        put("/api/role/update", HttpMethod.PUT);
        put("/api/dashboard/stats", HttpMethod.GET);
        put("/api/dashboard/recent-orders", HttpMethod.GET);
        put("/api/products", HttpMethod.POST);
        put("/api/products/*/upload-image", HttpMethod.POST);
        put("/api/user", HttpMethod.GET);
        put("/api/user/admin/create", HttpMethod.POST);
        put("/api/user/admin/*/role", HttpMethod.PUT);
        put("/api/user/admin/*/status", HttpMethod.PUT);
        put("/api/user/admin/*", HttpMethod.DELETE);
    }};
    /**
     * JWT secret key injected from application properties.
     */
    @Value("${jwt.secretkey}")
    private String secretkey;

    @Autowired
    private CustomJwtAuthenticationConverter jwtAuthenticationConverter;
    /**
     * Configures the security filter chain for HTTP requests.
     *
     * @param http the HttpSecurity object to configure
     * @return the configured SecurityFilterChain
     * @throws Exception if an error occurs during configuration
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()));

        http.oauth2ResourceServer(oauth2 -> oauth2.jwt(jwtConfigurer -> {
            jwtConfigurer.decoder(jwtDecoder());
            jwtConfigurer.jwtAuthenticationConverter(jwtAuthenticationConverter);
        }));

        http.authorizeHttpRequests(request -> {

            for (Map.Entry<String, HttpMethod> entry : PUBLIC_ENDPOINTS.entrySet()) {
                request.requestMatchers(new AntPathRequestMatcher(entry.getKey(), entry.getValue().name()))
                        .permitAll();
            }

            for (Map.Entry<String, HttpMethod> entry : ADMIN_ENDPOINTS.entrySet()) {
                request.requestMatchers(new AntPathRequestMatcher(entry.getKey(), entry.getValue().name()))
                        .hasAuthority("SCOPE_ADMIN");
            }
            
            request.requestMatchers(new AntPathRequestMatcher("/uploads/**")).permitAll();
            
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

    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder(10);
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200", "http://localhost:4201"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}