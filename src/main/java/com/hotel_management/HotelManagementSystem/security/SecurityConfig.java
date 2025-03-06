package com.hotel_management.HotelManagementSystem.security;


import com.hotel_management.HotelManagementSystem.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private JWTAuthFilter jwtAuthFilter;

    @Bean
    //Handling filter chain security
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception{
        // CSRF(Cross-Site Request Forgery) is unnecessary for stateless authentication (JWT-based)
        httpSecurity.csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults()) // Enables Cross-Origin Resource Sharing (CORS) with default settings
                .authorizeHttpRequests(request-> request
                        .requestMatchers("/auth/**","/bookings/**").permitAll()
                                .anyRequest().authenticated())
                .sessionManagement(manager -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) //No server-side session is created (JWT handles authentication instead)
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class); //Ensures JWT-based authentication is applied before UsernamePasswordAuthenticationFilter

        return httpSecurity.build();

    }

    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider(); //Uses a database to authenticate users
        daoAuthenticationProvider.setUserDetailsService(customUserDetailsService);//Loads user details (username, password, roles)
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        return daoAuthenticationProvider;

    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        //Uses BCryptPasswordEncoder to hash passwords securely, Encrypts passwords before storing them in the database
        return new BCryptPasswordEncoder();
    }

    @Bean
    //verifies user credentials and determines whether authentication is successful
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception{
            return  authenticationConfiguration.getAuthenticationManager();
    }
}
