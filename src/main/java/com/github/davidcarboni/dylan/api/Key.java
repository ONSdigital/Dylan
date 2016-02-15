package com.github.davidcarboni.dylan.api;

import com.github.davidcarboni.dylan.Store;
import com.github.davidcarboni.restolino.framework.Api;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.core.Response;
import java.io.IOException;

/**
 * Gets an encrypted key.
 */
@Api
public class Key {

	@GET
	public String get(HttpServletRequest request, HttpServletResponse response) throws IOException {

		String name = request.getParameter("name");
		String key = Store.getKey(name);

		if (key == null) {
			response.setStatus(Response.Status.NOT_FOUND.getStatusCode());
			return null;
		} else {
			return key;
		}
	}
}
