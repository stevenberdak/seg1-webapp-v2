package com.seg1.webapp.api.config;

import java.nio.charset.StandardCharsets;
import java.net.URLEncoder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.config.Customizer;

import com.seg1.webapp.api.repository.UserRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserRepository userRepository;

    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return (request, response, exception) -> {
            String errorMessage = "Invalid username or password";

            if (exception instanceof UsernameNotFoundException) {
                errorMessage = "Username does not exist";
            } else if (exception instanceof BadCredentialsException) {
                errorMessage = "Incorrect password";
            }

            String redirectUrl = "/sign-in.html?error=" + URLEncoder.encode(errorMessage, StandardCharsets.UTF_8);
            response.sendRedirect(redirectUrl);
        };
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/app/**").authenticated()
                        .anyRequest().permitAll())
                .formLogin(form -> form
                        .loginPage("/sign-in.html")
                        .loginProcessingUrl("/sign-in.html")
                        .defaultSuccessUrl("/app/browse-chatrooms.html")
                        .failureHandler(authenticationFailureHandler())
                        .permitAll())
                .logout(Customizer.withDefaults());
        return http.build();
    }

    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(username -> userRepository.findByUsername(username)
                        .map(user -> User.builder()
                                .username(user.getUsername())
                                .password(user.getPasswordHash())
                                .roles("USER") // Add roles if necessary
                                .build())
                        .orElseThrow(() -> new BadCredentialsException("User not found")))
                .passwordEncoder(new BCryptPasswordEncoder())
                .and()
                .build();
    }
}