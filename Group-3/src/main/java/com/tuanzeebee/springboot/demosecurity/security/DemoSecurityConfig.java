package com.tuanzeebee.springboot.demosecurity.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
public class DemoSecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

    @Autowired
    public DemoSecurityConfig(CustomUserDetailsService userDetailsService, 
                             CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler) {
        this.userDetailsService = userDetailsService;
        this.customAuthenticationSuccessHandler = customAuthenticationSuccessHandler;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
        auth.setUserDetailsService(userDetailsService);
        auth.setPasswordEncoder(passwordEncoder());
        return auth;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(configurer ->
                configurer
                    // Các trang công khai
                    .requestMatchers("/", "/login", "/register/**").permitAll()
                    .requestMatchers("/requirement-food", "/relax", "/blog").permitAll()
                    .requestMatchers("/relax/**").permitAll()
                    
                    // Tài nguyên tĩnh và trang lỗi
                    .requestMatchers("/assets/**", "/css/**", "/js/**", "/images/**", "/libs/**").permitAll()
                    .requestMatchers("/error/**", "/error").permitAll()
                    
                    // API endpoints
                    .requestMatchers("/api/recipes/**", "/api/ingredients/**").permitAll()
                    .requestMatchers("/api/admin/**").hasRole("ADMIN")
                    .requestMatchers("/api/manager/**").hasRole("MANAGER")
                    
                    // Các trang yêu cầu xác thực
                    .requestMatchers("/admin/**").hasRole("ADMIN")
                    .requestMatchers("/manager/**").hasRole("MANAGER")
                    .requestMatchers("/profile/**").authenticated()
                    .anyRequest().authenticated()
            )
            .formLogin(form ->
                form
                    .loginPage("/login")
                    .loginProcessingUrl("/authenticateTheUser")
                    .successHandler(customAuthenticationSuccessHandler)
                    .permitAll()
            )
            .logout(logout -> 
                logout
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/login?logout")
                    .permitAll()
            )
            .exceptionHandling(configurer ->
                configurer
                    .accessDeniedPage("/error/access-denied")
            )
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/api/**")
            );
            
        return http.build();
    }
}
