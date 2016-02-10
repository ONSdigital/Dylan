package com.github.davidcarboni.dylan.api;

import com.github.davidcarboni.cryptolite.KeyWrapper;
import com.github.davidcarboni.restolino.framework.Api;
import com.github.davidcarboni.restolino.helpers.Path;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import java.security.PublicKey;

/**
 * Gets an encrypted key.
 */
@Api
public class Key {

    @GET
    public String get(HttpServletRequest request, HttpServletResponse response) {
        String filename = Path.newInstance(request).lastSegment();
        PublicKey dummy = com.github.davidcarboni.cryptolite.Keys.newKeyPair().getPublic();
        return KeyWrapper.encodePublicKey();
    }
}
