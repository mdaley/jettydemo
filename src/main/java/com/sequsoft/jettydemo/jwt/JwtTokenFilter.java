package com.sequsoft.jettydemo.jwt;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationEntryPointFailureHandler;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A filter that attempts authentication using JWT.
 */
public class JwtTokenFilter extends OncePerRequestFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtTokenFilter.class);

    private final AuthenticationManager authenticationManager;

    public JwtTokenFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String authHeader = request.getHeader(AUTHORIZATION);

        Authentication successfulAuthentication = null;

        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            String[] parts = authHeader.split(" ");
            if (parts.length == 2) {
                String token = parts[1];

                if (StringUtils.hasText(token)) {
                    try {
                        successfulAuthentication = authenticationManager.authenticate(new JwtAuthentication(token));
                        SecurityContextHolder.getContext().setAuthentication(successfulAuthentication);
                    } catch (AuthenticationException e) {
                        SecurityContextHolder.clearContext();
                    }
                }
            }
        }

        if (successfulAuthentication == null) {
            new AuthenticationEntryPointFailureHandler(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                    .onAuthenticationFailure(request, response, new BadCredentialsException("Bad token"));
        }

        chain.doFilter(request, response);
    }
}
