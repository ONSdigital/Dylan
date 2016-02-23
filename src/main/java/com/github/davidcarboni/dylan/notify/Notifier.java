package com.github.davidcarboni.dylan.notify;

import com.github.davidcarboni.dylan.filesystem.CryptoPath;
import com.github.davidcarboni.httpino.Endpoint;
import com.github.davidcarboni.httpino.Http;
import com.github.davidcarboni.httpino.Response;
import org.apache.http.StatusLine;

import java.io.IOException;
import java.nio.file.Path;

import static com.github.davidcarboni.dylan.Configuration.Recipient.RECIPIENT_HOST;
import static com.github.davidcarboni.dylan.Configuration.Recipient.RECIPIENT_NOTIFY_PATH;
import static com.github.davidcarboni.dylan.Configuration.getEndpoint;

/**
 * Notifies the recipient that a file has been received.
 */
public class Notifier {

	public static final Endpoint endpoint = getEndpoint(RECIPIENT_HOST, RECIPIENT_NOTIFY_PATH);

	public static boolean notify(Path path) throws IOException {
		boolean result = false;
		String name = CryptoPath.unwrap(path).getFileName().toString();

		try (Http http = new Http()) {
			Response<String> response = http.postJson(endpoint, name, String.class);
			StatusLine statusLine = response.statusLine;
			if (statusLine.getStatusCode() == 200) {
				result = true;
			} else {
				System.out.println("Error notifying the recipient: " + statusLine.getStatusCode() + " " + statusLine.getReasonPhrase());
			}
		}
		return result;
	}
}
