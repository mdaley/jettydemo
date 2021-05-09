package com.sequsoft.jettydemo;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.Verification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.web.authentication.AuthenticationEntryPointFailureHandler;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.security.interfaces.RSAPublicKey;
import java.util.Optional;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtTokenFilter.class);

    private final JwtUtils jwtUtils;

    @Autowired
    public JwtTokenFilter(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String authorization = request.getHeader(AUTHORIZATION);
        if (StringUtils.hasText(authorization) && authorization.startsWith("Bearer ")) {
            String[] parts = authorization.split(" ");
            if (parts.length == 2) {
                String token = parts[1];

                Optional<DecodedJWT> decodedJwt = jwtUtils.verifyToken(token);

                if (decodedJwt.isEmpty()) {
                    new AuthenticationEntryPointFailureHandler(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                            .onAuthenticationFailure(request, response, new BadCredentialsException("Bad token"));
                } else {
                    // checking the token
                    // adding it to security context

                }
            }
        }

        chain.doFilter(request, response);
    }
}
