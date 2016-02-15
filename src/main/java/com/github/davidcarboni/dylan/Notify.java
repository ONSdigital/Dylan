package com.github.davidcarboni.dylan;

import com.github.davidcarboni.httpino.Endpoint;
import com.github.davidcarboni.httpino.Http;
import com.github.davidcarboni.httpino.Response;
import org.apache.http.StatusLine;

import java.io.IOException;

import static com.github.davidcarboni.dylan.Configuration.RECIPIENT_HOST;
import static com.github.davidcarboni.dylan.Configuration.getEndpoint;

/**
 * Notifies the recipient that a file has been received.
 */
public class Notify {

    public static final String RECIPIENT_NOTIFY_PATH = "notify";
    public static final Endpoint endpoint = getEndpoint(RECIPIENT_HOST, RECIPIENT_NOTIFY_PATH);

    public static boolean notify(String name) throws IOException {
        boolean result = false;

        try (Http http = new Http()) {
            Response<String> response = http.getJson(endpoint.setParameter("name", name), String.class);
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
