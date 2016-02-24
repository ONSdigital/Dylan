package com.github.davidcarboni.dylan.notify;

import com.github.davidcarboni.dylan.api.HttpSupplier;
import com.github.davidcarboni.httpino.Endpoint;
import com.github.davidcarboni.httpino.Http;
import com.github.davidcarboni.httpino.Response;
import org.apache.http.StatusLine;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Path;

import static com.github.davidcarboni.dylan.Configuration.Recipient.RECIPIENT_HOST;
import static com.github.davidcarboni.dylan.Configuration.Recipient.RECIPIENT_NOTIFY_PATH;
import static com.github.davidcarboni.dylan.Configuration.getEndpoint;
import static javax.ws.rs.core.Response.Status.OK;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Notifies the recipient that a file has been received.
 */
public class Notifier {

	private static final Logger LOG = getLogger(Notifier.class);
	public static final Endpoint endpoint = getEndpoint(RECIPIENT_HOST, RECIPIENT_NOTIFY_PATH);

	private Notifier() {
		// Utility class hide constructor.
	}

	public static void notify(Path filename, HttpSupplier httpSupplier) throws IOException {
		String name = filename.getFileName().toString();

		try (Http http = httpSupplier.get()) {
			Response<String> response = http.postJson(endpoint, name, String.class);
			StatusLine statusLine = response.statusLine;
			if (statusLine.getStatusCode() != OK.getStatusCode()) {
				LOG.info("Error notifying the recipient: {} {}", statusLine.getStatusCode(), statusLine.getReasonPhrase());
				throw new IOException(statusLine.getReasonPhrase());
			}
		}
	}
}
