package com.github.davidcarboni.dylan;

import com.github.davidcarboni.cryptolite.KeyWrapper;
import com.github.davidcarboni.httpino.Endpoint;
import com.github.davidcarboni.httpino.Http;
import com.github.davidcarboni.httpino.Response;
import com.github.davidcarboni.restolino.framework.Startup;

import java.io.IOException;
import java.security.PublicKey;
import java.util.Optional;

import static com.github.davidcarboni.dylan.Configuration.RECIPIENT_HOST;
import static com.github.davidcarboni.dylan.Configuration.getEndpoint;
import static javax.ws.rs.core.Response.Status.OK;

/**
 * Requests the public key from Zebedee.
 */
public class GetZebedeeKey implements Startup {

	public static final String RECIPIENT_KEY_PATH = "csdbkey";
	public static final Endpoint endpoint = getEndpoint(RECIPIENT_HOST, RECIPIENT_KEY_PATH);

	@Override
	public void init() {
		Optional<String> zebedeeKey = getKey();
		if (zebedeeKey.isPresent()) {
			PublicKey publicKey = KeyWrapper.decodePublicKey(zebedeeKey.get());
			try {
				Store.saveRecipientKey(publicKey);
				System.out.println("Zebedee public key obtained/updated.");
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		} else {
			// TODO RETRY
			throw new RuntimeException("FAILED TO OBTAIN KEY.");
		}
	}

	private Optional<String> getKey() {
		try (Http http = new Http()) {
			Response<String> response = http.getJson(endpoint, String.class);
			if (response.statusLine.getStatusCode() == OK.getStatusCode()) {
				return Optional.of(response.body);
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return Optional.empty();
	}
}
