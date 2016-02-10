package com.github.davidcarboni.dylan;

import com.github.davidcarboni.cryptolite.KeyWrapper;
import com.github.davidcarboni.cryptolite.Keys;
import com.github.davidcarboni.restolino.framework.Startup;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.PublicKey;

/**
 * Requests the public key from Zebedee.
 */
public class GetZebedeeKey implements Startup {
    @Override
    public void init() {
        PublicKey dummy = Keys.newKeyPair().getPublic();
        String encodedKey = KeyWrapper.encodePublicKey(dummy);
        try {
            Path cached = Files.createTempFile("recipient", ".pub");
            FileUtils.writeStringToFile(cached.toFile(), encodedKey, StandardCharsets.UTF_8);
            Files.move(cached, Storage.recipientKey, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Foo.");
        }
    }
}
