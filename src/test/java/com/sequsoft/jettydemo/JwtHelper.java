package com.sequsoft.jettydemo;

import ch.qos.logback.core.util.COWArrayList;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.impl.PublicClaims;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class JwtHelper {
    private static final Set<String> STR_PUBLIC_CLAIMS = Set.of(PublicClaims.ISSUER, PublicClaims.SUBJECT, PublicClaims.JWT_ID);
    private static final Set<String> DATE_PUBLIC_CLAIMS = Set.of(PublicClaims.NOT_BEFORE, PublicClaims.EXPIRES_AT, PublicClaims.ISSUED_AT);

    private RSAPublicKey publicKey;
    private RSAPrivateKey privateKey;
    private Algorithm algorithm;

    public JwtHelper() {
        setupJwtKeysAndAlgorithm();
    }

    public RSAPublicKey getPublicKey() {
        return publicKey;
    }

    public RSAPrivateKey getPrivateKey() {
        return privateKey;
    }

    private void setupJwtKeysAndAlgorithm() {
        KeyPairGenerator gen;
        try {
            gen = KeyPairGenerator.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        gen.initialize(2048);

        KeyPair pair = gen.generateKeyPair();
        publicKey = (RSAPublicKey) pair.getPublic();
        privateKey = (RSAPrivateKey) pair.getPrivate();
        algorithm = Algorithm.RSA256(publicKey, privateKey);
    }

    public String createJwt(Map<String, Object> claims) {
        JWTCreator.Builder builder = JWT.create();

        if (claims != null) {
            claims.forEach((k, v) -> {
                // this is horrid but that's Java for you!
                if (STR_PUBLIC_CLAIMS.contains(k)) {
                    builder.withClaim(k, (String) v);
                } else if (DATE_PUBLIC_CLAIMS.contains(k)) {
                    builder.withClaim(k, (Date) v);
                } else if (PublicClaims.AUDIENCE.equals(k)) {
                    builder.withAudience((String[]) v);
                } else if (PublicClaims.KEY_ID.equals(k)) {
                    builder.withKeyId((String) v);
                } else if (v instanceof Boolean v_) {
                    builder.withClaim(k, v_);
                } else if (v instanceof Integer v_) {
                    builder.withClaim(k, v_);
                } else if (v instanceof Long v_) {
                    builder.withClaim(k, v_);
                } else if (v instanceof Double v_) {
                    builder.withClaim(k, v_);
                } else if (v instanceof String v_) {
                    builder.withClaim(k, v_);
                } else if (v instanceof Date v_) {
                    builder.withClaim(k, v_);
                } else if (v instanceof Map v_) {
                    builder.withClaim(k, v_);
                } else if (v instanceof List v_) {
                    builder.withClaim(k, v_);
                } else if (v instanceof Long[] v_) {
                    builder.withArrayClaim(k, v_);
                } else if (v instanceof Integer[] v_) {
                    builder.withArrayClaim(k, v_);
                } else if (v instanceof String[] v_) {
                    builder.withArrayClaim(k, v_);
                }
            });
        }

        return builder.sign(algorithm);
    }
}
