package edu.kingston.agriconnect.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final MyUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain

    ) throws ServletException, IOException {

//        if (request.getServletPath().contains("/api/**")) { //! for testing
//            filterChain.doFilter(request, response);
//        }
//        if (request.getServletPath().contains("/api/auth/**")) {
//            filterChain.doFilter(request, response);
//        }

        if (request.getServletPath().contains("/v2/api-docs")) {
            filterChain.doFilter(request, response);
        }
        if (request.getServletPath().contains("/v3/api-docs")) {
            filterChain.doFilter(request, response);
        }
        if (request.getServletPath().contains("/v3/api-docs/**")) {
            filterChain.doFilter(request, response);
        }
        if (request.getServletPath().contains("/swagger-resources")) {
            filterChain.doFilter(request, response);
        }
        if (request.getServletPath().contains("/swagger-resources/**")) {
            filterChain.doFilter(request, response);
        }
        if (request.getServletPath().contains("/configuration/ui")) {
            filterChain.doFilter(request, response);
        }
        if (request.getServletPath().contains("/configuration/security")) {
            filterChain.doFilter(request, response);
        }
        if (request.getServletPath().contains("/swagger-ui.html")) {
            filterChain.doFilter(request, response);
        }
        if (request.getServletPath().contains("/swagger-ui/index.html")) {
            filterChain.doFilter(request, response);
        }
        if (request.getServletPath().contains("/swagger-ui")) {
            filterChain.doFilter(request, response);
        }

        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String jwt;
        final String userEmail;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        jwt = authHeader.substring(7);
        userEmail = jwtService.extractUsername(jwt);

        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
            if (jwtService.isTokenValid(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}
