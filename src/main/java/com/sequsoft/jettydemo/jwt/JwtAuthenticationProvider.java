package com.sequsoft.jettydemo.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.sequsoft.jettydemo.User;
import com.sequsoft.jettydemo.plugins.JwtPublicKeyPlugin;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

/**
 * Provides the mechanism for authenticating JWTs.
 */
public class JwtAuthenticationProvider implements AuthenticationProvider {
    private final JwtPublicKeyPlugin jwtPublicKeyPlugin;

    private JWTVerifier verifier;

    public JwtAuthenticationProvider(JwtPublicKeyPlugin jwtPublicKeyPlugin) {
        this.jwtPublicKeyPlugin = jwtPublicKeyPlugin;
        createVerifier();
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (authentication instanceof JwtAuthentication jwtAuthentication) {
            try {
                DecodedJWT verifiedToken = verifyToken(jwtAuthentication.getToken());

                User user = new User();
                user.setSubject(verifiedToken.getSubject());
                user.setUsername(verifiedToken.getSubject());

                jwtAuthentication.setUser(user);
                jwtAuthentication.setAuthenticated(true);

                return jwtAuthentication;
            } catch (JWTVerificationException e) {
                throw new BadCredentialsException("Invalid token", e);
            }
        }

        return null;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtAuthentication.class.isAssignableFrom(authentication);
    }

    public DecodedJWT verifyToken(String token) {
        return verifier.verify(token);
    }

    private void createVerifier() {
        verifier = JWT.require(Algorithm.RSA256(jwtPublicKeyPlugin.getPublicKey(), null)).build();
    }
}
