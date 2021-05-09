package com.sequsoft.jettydemo;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class JwtUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtUtils.class);

    private final JwtPublicKeyProvider jwtPublicKeyProvider;

    private JWTVerifier verifier;

    public JwtUtils(JwtPublicKeyProvider jwtPublicKeyProvider) {
        this.jwtPublicKeyProvider = jwtPublicKeyProvider;
        createVerifier();
    }

    public Optional<DecodedJWT> verifyToken(String token) {
        try {
            return Optional.of(verifier.verify(token));
        } catch (JWTVerificationException e) {
            return Optional.empty();
        }
    }

    private void createVerifier() {
        verifier = JWT.require(Algorithm.RSA256(jwtPublicKeyProvider.getPublicKey(), null)).build();
    }
}
