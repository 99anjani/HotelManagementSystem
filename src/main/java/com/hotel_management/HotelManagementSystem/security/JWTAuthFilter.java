package com.hotel_management.HotelManagementSystem.security;


import com.hotel_management.HotelManagementSystem.service.CustomUserDetailsService;
import com.hotel_management.HotelManagementSystem.utils.JWTUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JWTAuthFilter extends OncePerRequestFilter {
    //Handles extracting and validating JWT tokens.
    @Autowired
    private JWTUtils jwtUtils;

    //Loads user details from the database.
    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        final String jwtToken;
        final String userEmail;

        if (authHeader == null || authHeader.isBlank()){
            filterChain.doFilter(request,response);
            return;
        }

        jwtToken = authHeader.substring(7); // Removes the "Bearer " prefix, leaving only the JWT token
        userEmail = jwtUtils.extractUsername(jwtToken);

        //Check already authenticated or not
        if(userEmail != null && SecurityContextHolder.getContext().getAuthentication()== null){

            UserDetails userDetails = customUserDetailsService.loadUserByUsername(userEmail);//ensures that the user exists and has the right permissions

            //Token validation (not expired, belongs to the correct user,...)
            if(jwtUtils.isValidToken(jwtToken,userDetails)) {

                //Creating an Authentication Token

                SecurityContext securityContext = SecurityContextHolder.createEmptyContext(); //Creates a new, empty security context that will store authentication details
                UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request)); // Attaches request-specific authentication details (like IP and session ID) to the user token
                securityContext.setAuthentication(token); // Registers the authenticated user into the Spring Security context.
                SecurityContextHolder.setContext(securityContext); // Saves the security context in SecurityContextHolder to allow role-based access throughout the request.
            }
        }
        filterChain.doFilter(request,response);

    }
}

