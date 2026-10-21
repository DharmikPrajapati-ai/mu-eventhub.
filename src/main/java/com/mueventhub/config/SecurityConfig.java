package com.mueventhub.config;

import com.mueventhub.repo.RepresentativeRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/rep/**").hasRole("REP")
                .anyRequest().permitAll())
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/rep/dashboard", true)
                .permitAll())
            .logout(logout -> logout.logoutSuccessUrl("/?loggedout"));
        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    UserDetailsService userDetailsService(RepresentativeRepository reps) {
        return username -> reps.findByUsername(username)
                .map(r -> User.withUsername(r.getUsername())
                        .password(r.getPasswordHash())
                        .roles("REP")
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("No such representative"));
    }
}
