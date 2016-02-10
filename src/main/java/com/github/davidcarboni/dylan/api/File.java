package com.github.davidcarboni.dylan.api;

import com.github.davidcarboni.cryptolite.Random;
import com.github.davidcarboni.restolino.helpers.Path;
import org.apache.commons.io.IOUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import java.io.IOException;
import java.io.InputStream;

/**
 * Gets an encrypted file.
 */
public class File {

    @GET
    public void get(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String filename = Path.newInstance(request).lastSegment();
        try (InputStream dummy = Random.inputStream(1024)) {
            IOUtils.copy(dummy, response.getOutputStream());
        }
    }
}
