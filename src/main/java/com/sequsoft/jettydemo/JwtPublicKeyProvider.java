package com.sequsoft.jettydemo;

import java.security.interfaces.RSAPublicKey;

public interface JwtPublicKeyProvider {
    /**
     * Returns the public key used tfor JWT signing. For a real implementation, this interface most likely encapsulates
     * a call to the remote system than manages JWTs for an organisation. For tests in can just return a canned value.
     * @return
     */
    RSAPublicKey getPublicKey();
}
