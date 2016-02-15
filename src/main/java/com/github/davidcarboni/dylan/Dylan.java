package com.github.davidcarboni.dylan;

import com.github.davidcarboni.cryptolite.KeyExchange;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.security.PublicKey;

/**
 * Handles the work of encrypting and storing keys for received files.
 */
public class Dylan {

      public static void storeKey(String name, SecretKey key) throws IOException {
          PublicKey recipientKey = Storage.getRecipientKey();
          KeyExchange keyExchange = new KeyExchange();
          String encryptedKey = keyExchange.encryptKey(key, recipientKey);
          //Storage.saveKey(encryptedKey);
      }
}
