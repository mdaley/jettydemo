package com.sequsoft.jettydemo.plugins;

import java.security.interfaces.RSAPublicKey;

/**
 * A plugin for obtaining the public key used for authenticating JWT signatures. For a real implementation, this interface
 * most likely would encapsulate a call to a remote system than manages JWTs for an organisation. For tests it might just
 * return a canned value.
 */
public interface JwtPublicKeyPlugin {
    /**
     * @return the public key
     */
    RSAPublicKey getPublicKey();
}
