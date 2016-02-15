package com.github.davidcarboni.dylan.sshd;

import java.security.PublicKey;

@FunctionalInterface
public interface PublicKeyAuthenticator {

	boolean isValid(String user, PublicKey key);
}
