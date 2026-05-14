package com.example.project.filter;

import com.example.project.service.CustomUserDetailsService;
import com.example.project.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@RequiredArgsConstructor
@Component
public class JwtFilter  extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException
    {
        String authHeader = request.getHeader("Authorization");

        if(authHeader == null || !authHeader.startsWith("Bearer "))
        {
            // login apis has no tokens so skip the authentication filetr ,next goes to auth filetr ,there in the authorzation
            // we have configured like .requestMatcher("/auth/**").permitAll()
            filterChain.doFilter(request,response);
            return;
        }

        String jwtToken = authHeader.substring(7);
        System.out.println(jwtToken);

        String username = jwtService.extractUsername(jwtToken);
        System.out.println(username);

        if(username != null
                &&
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        == null)
        {
            UserDetails userDetails =
                    customUserDetailsService
                            .loadUserByUsername(
                                    username
                            );

            if(jwtService.isTokenValid(jwtToken, userDetails.getUsername()))
            {
                System.out.println("TOKEN VALID");
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                SecurityContextHolder
                        .getContext()
                        .setAuthentication(
                                authToken
                        );
                System.out.println(SecurityContextHolder
                        .getContext()
                        .getAuthentication());
            }
        }

        filterChain.doFilter(
                request,
                response
        );
    }
    /*
      reqst
        |
      filter
        |
      controller

     -> this should happen
     -> but without this OncePerRequestFilter ,reqst may go to filter chain for every operation
     example

     Browser Request
            ↓
    JWT Filter
            ↓
    Controller
            ↓
    Error 💥
            ↓
    Spring forwards internally
            ↓
    /error

 -> so here spring internally sed that rqt ro error/ so here again filter check happens if we dont extends that
 which is unnecessary and leads to bugs
     */
}
