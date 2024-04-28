package com.woobot.feedbackservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;

@Configuration
public class SecurityBeans {
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .authorizeExchange(configurer -> configurer
                        .pathMatchers("/webjars/**", "/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**")
                        .permitAll()
                        .anyExchange().authenticated())
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                .oauth2ResourceServer(customizer -> customizer.jwt(Customizer.withDefaults()))
                .build();
    }
}

//.authorizeExchange(): Этот метод настраивает правила авторизации для обмена данными. configurer -> configurer
// .anyExchange().authenticated() означает, что все обмены должны быть аутентифицированы, чтобы быть выполненными.
//
// .csrf(): Этот метод настраивает защиту от подделки межсайтовых запросов (CSRF).
// ServerHttpSecurity.CsrfSpec::disable отключает CSRF.
//
// .securityContextRepository(): Этот метод настраивает хранилище контекста безопасности.
// NoOpServerSecurityContextRepository.getInstance() использует пустое (не делающее ничего) хранилище
// контекста безопасности.
//
//.oauth2ResourceServer(): Этот метод настраивает сервер ресурсов OAuth2. customizer -> customizer.jwt(
// Customizer.withDefaults()) настраивает сервер ресурсов для проверки токена JWT (JSON Web Token) с использованием
// настроек по умолчанию.