package com.github.davidcarboni.dylan.api;

import com.github.davidcarboni.dylan.Store;
import com.github.davidcarboni.restolino.framework.Api;
import org.apache.commons.io.IOUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;

/**
 * Gets an encrypted file.
 */
@Api
public class File {

	@GET
	public void get(HttpServletRequest request, HttpServletResponse response) throws IOException {

		String name = request.getParameter("name");

		try (InputStream file = Store.getFile(name)) {
			if (file == null) {
				response.setStatus(Response.Status.NOT_FOUND.getStatusCode());
			} else {
				IOUtils.copy(file, response.getOutputStream());
			}
		}
	}
}
