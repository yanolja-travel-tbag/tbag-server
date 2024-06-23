package com.tbag.tbag_backend.config;

import com.tbag.tbag_backend.domain.Auth.handler.OAuth2AuthenticationFailureHandler;
import com.tbag.tbag_backend.domain.Auth.handler.OAuth2AuthenticationSuccessHandler;
import com.tbag.tbag_backend.domain.Auth.jwt.JwtAccessDeniedHandler;
import com.tbag.tbag_backend.domain.Auth.jwt.JwtAuthenticationEntryPoint;
import com.tbag.tbag_backend.domain.Auth.jwt.TokenProvider;
import com.tbag.tbag_backend.domain.Auth.jwt.filter.JwtFilter;
import com.tbag.tbag_backend.domain.Auth.jwt.filter.RefreshFilter;
import com.tbag.tbag_backend.domain.Auth.service.CustomOAuth2UserService;
import com.tbag.tbag_backend.domain.User.repository.UserRepository;
import com.tbag.tbag_backend.exception.GlobalExceptionHandlerFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final RedisTemplate<String, String> redisTemplate;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors().configurationSource(corsConfigurationSource())
                .and()
                .csrf().disable()
                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler)
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()

                .authorizeRequests()
                .antMatchers("/", "/exception/**", "/error", "/favicon.ico",
                        "/auth/login", "/auth/signup",
                        "/oauth2/**",
                        "/public/**")
                .permitAll()
                .antMatchers("/user/*/update-registration", "/user/*/deactivate","/genres", "/artists")
                .hasAnyRole("USER", "GUEST")
                .anyRequest().hasRole("USER")
                .and()
                .addFilterBefore(new JwtFilter(tokenProvider), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new RefreshFilter(tokenProvider, redisTemplate), JwtFilter.class)
                .addFilterBefore(new GlobalExceptionHandlerFilter(), RefreshFilter.class)
                .oauth2Login()
                .authorizationEndpoint()
                .and()
                .userInfoEndpoint().userService(customOAuth2UserService)
                .and()
                .successHandler(oAuth2AuthenticationSuccessHandler)
                .failureHandler(oAuth2AuthenticationFailureHandler);

        return http.build();
    }


    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("HEAD", "POST", "GET", "DELETE", "PUT"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new Argon2PasswordEncoder();
    }

}
