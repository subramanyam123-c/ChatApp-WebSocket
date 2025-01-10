package com.dev.ChatApp.Filter;

import com.dev.ChatApp.JWT.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    private static final Logger logger = LoggerFactory.getLogger(JwtRequestFilter.class);
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();
        String queryString = request.getQueryString();
        logger.debug("JwtRequestFilter shouldNotFilter checking path: " + path + ", query: " + queryString);

        // Exclude public endpoints and WebSocket handshake
        return path.equals("/error")
                || path.equals("/favicon.ico")
                || path.startsWith("/api/users/login")
                || path.startsWith("/api/users/create")
                || path.startsWith("/ws/")
                || path.matches("/ws/.*")
                || path.startsWith("/h2-console/")
                || path.startsWith("/swagger-ui/")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/swagger-resources/")
                || path.startsWith("/webjars/");
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        final String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            logger.debug("JWT Token extracted: {}", jwt);
            username = jwtUtil.extractUsername(jwt);
            logger.debug("Username extracted from token: {}", username);
        } else {
            logger.warn("Authorization header missing or does not start with 'Bearer '");
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            if (jwtUtil.validateToken(jwt)) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(username, null, new ArrayList<>());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
                logger.info("Authentication set for user: {}", username);
            } else {
                logger.error("Invalid JWT token for user: {}", username);
            }
        }
        chain.doFilter(request, response);
    }
}
