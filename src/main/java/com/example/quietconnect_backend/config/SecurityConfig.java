package com.example.quietconnect_backend.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.example.quietconnect_backend.auth.OAuthSuccessHandler;
import com.example.quietconnect_backend.jwt.JwtAuthFilter;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final OAuthSuccessHandler oAuthSuccessHandler;
    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(
            OAuthSuccessHandler oAuthSuccessHandler,
            JwtAuthFilter jwtAuthFilter
    ) {
        this.oAuthSuccessHandler = oAuthSuccessHandler;
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            // ================== CORS ==================
            .cors(Customizer.withDefaults())

            // ================== CSRF ==================
            .csrf(csrf -> csrf.disable())

            // ================== SESSION ==================
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // ================== EXCEPTION ==================
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) ->
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED)
                )
            )

            // ================== ROUTE SECURITY ==================
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/",
                    "/oauth2/**",
                    "/login/**",
                    "/error",
                    "/public/**",
                    "/chat/**",
                    "/ws/**",
                    "/app/**",
                    "/user/**",
                    "/topic/**",
                    "/queue/**","/private-message",
                    "/presence/**","/auth/**"
                ).permitAll()
                .requestMatchers("/api/**","/getFriend","/getOldchat/**").authenticated()
                .anyRequest().authenticated()
            )

            // ================== JWT FILTER ==================
            .addFilterBefore(
                jwtAuthFilter,
                UsernamePasswordAuthenticationFilter.class
            )

            // ================== OAUTH2 LOGIN ==================
            .oauth2Login(oauth ->
                oauth.successHandler(oAuthSuccessHandler)
            )

            // ================== LOGOUT ==================
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessHandler((request, response, authentication) -> {
                    response.setHeader(
                        "Set-Cookie",
                        "token=; HttpOnly; Path=/; Max-Age=0; SameSite=Lax;"
                    );
                    response.setStatus(HttpServletResponse.SC_OK);
                })
                .permitAll()
            );

        return http.build();
    }

    // ================== CORS CONFIG ==================

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOriginPatterns(List.of("http://localhost:3000"));
        config.setAllowedMethods(
            List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
        );
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", config);

        return source;
    }
}