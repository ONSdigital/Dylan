package com.github.davidcarboni.dylan;

import com.github.davidcarboni.cryptolite.KeyWrapper;
import com.github.davidcarboni.httpino.Endpoint;
import com.github.davidcarboni.httpino.Http;
import com.github.davidcarboni.httpino.Response;
import com.github.davidcarboni.restolino.framework.Startup;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import java.io.IOException;
import java.security.PublicKey;

import static com.github.davidcarboni.dylan.Configuration.Recipient.RECIPIENT_HOST;
import static com.github.davidcarboni.dylan.Configuration.Recipient.RECIPIENT_KEY_PATH;
import static com.github.davidcarboni.dylan.Configuration.getEndpoint;
import static javax.ws.rs.core.Response.Status.OK;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Requests the public key from Zebedee.
 */
public class GetZebedeeKey implements Startup {

	private static final Logger LOG = getLogger(GetZebedeeKey.class);
	public static final Endpoint endpoint = getEndpoint(RECIPIENT_HOST, RECIPIENT_KEY_PATH);

	@Override
	public void init() {
		try {
			if (recipientKeyExists()) {
				LOG.info("Existing recipient key found. No action further action required.");
			} else {
				LOG.info("No existing recipient key found on start up. Requesting a new recipient key.");
				String recipientKeyStr = requestKeyFromRecipient();
				PublicKey publicKey = KeyWrapper.decodePublicKey(recipientKeyStr);
				Store.saveRecipientKey(publicKey);
				LOG.info("Successfully obtained & stored recipient key. Continuing with start up.");
			}
		} catch (IOException e) {
			LOG.info("Encountered an unexpected error: {}", e);
		}
	}

	/**
	 * Check if a recipient is available.
	 */
	private boolean recipientKeyExists() throws IOException {
		return Store.getRecipientKey().isPresent();
	}

	/**
	 * Get the recipient public key from the recipient.
	 */
	private String requestKeyFromRecipient() throws IOException {
		try (Http http = new Http()) {
			Response<String> response = http.getJson(endpoint, String.class);

			if (response.statusLine.getStatusCode() != OK.getStatusCode()) {
				LOG.info("Failed to obtain recipient key. HTTP status code {}", response.statusLine.getStatusCode());
				throw new RuntimeException();
			}
			if (StringUtils.isEmpty(response.body)) {
				LOG.info("Failed to obtain recipient key. Response body was empty");
				throw new RuntimeException();
			}
			return response.body;
		} catch (IOException e) {
			LOG.info("Unexpected error while requesting new recipient key: {}", e);
			throw e;
		}
	}
}
