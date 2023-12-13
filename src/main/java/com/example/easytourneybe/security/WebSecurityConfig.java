package com.example.easytourneybe.security;


import com.example.easytourneybe.enums.UserRole;
import com.example.easytourneybe.security.jwt.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.session.SessionManagementFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {
    @Autowired
    private JwtFilter jwtFilter;

    @Autowired
    private CorsConfig config;
    @Autowired
    private final AuthenticationProvider authenticationProvider;
    private static final String[] WHITE_LIST_URL = {"/auth/**"};
    private static final String[] SWAGGER_LIST_URL = {"/swagger-ui/**", "/*/swagger-resources/**", "/v2/api-docs","/webjars/**",  "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs", "/v3/api-docs/**"};
    private static final String[] ONLY_ADMIN_LIST_URL = {"/category/**", "/organizer/**"};

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(config, SessionManagementFilter.class)
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(request ->
                        request
                                .requestMatchers(WHITE_LIST_URL).permitAll()
                                .requestMatchers(SWAGGER_LIST_URL).permitAll()
                                .requestMatchers(HttpMethod.POST, ONLY_ADMIN_LIST_URL).hasAuthority(UserRole.ADMIN.name())
                                .requestMatchers(HttpMethod.PUT, ONLY_ADMIN_LIST_URL).hasAuthority(UserRole.ADMIN.name())
                                .requestMatchers(HttpMethod.DELETE, ONLY_ADMIN_LIST_URL).hasAuthority(UserRole.ADMIN.name())
                                .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider);
        return http.build();
    }

}
