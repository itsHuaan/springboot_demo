package org.example.springboot_demo.configurations;

import org.example.springboot_demo.configurations.exception.Forbidden;
import org.example.springboot_demo.configurations.exception.Unauthorized;
import org.example.springboot_demo.configurations.jwtConfig.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Lazy
    @Autowired
    JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    Unauthorized unauthorized;

    @Autowired
    Forbidden forbidden;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(requests -> {
                    try {
                        requests
                                .requestMatchers(new AntPathRequestMatcher("/public/**"),
                                        new AntPathRequestMatcher("/error"),
                                        new AntPathRequestMatcher("/login"),
                                        new AntPathRequestMatcher("/css/**"),
                                        new AntPathRequestMatcher("/images/**"),
                                        new AntPathRequestMatcher("/js/**"),
                                        new AntPathRequestMatcher("/vendors/**"),
                                        new AntPathRequestMatcher("/**"))
                                .permitAll()
                                .anyRequest()
                                .authenticated();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
