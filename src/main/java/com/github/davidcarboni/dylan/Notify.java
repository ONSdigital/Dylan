package com.github.davidcarboni.dylan;

import com.github.davidcarboni.httpino.Endpoint;
import com.github.davidcarboni.httpino.Http;
import com.github.davidcarboni.httpino.Response;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.StatusLine;

import java.io.IOException;

/**
 * Notifies the recipient that a file has been received.
 */
public class Notify {

    static final Endpoint endpoint = new Endpoint(StringUtils.defaultIfBlank(System.getenv("recipient.url"), "http://localhost:8080/notify"));

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
