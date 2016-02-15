package com.github.davidcarboni.dylan.api;

import com.github.davidcarboni.dylan.Store;
import com.github.davidcarboni.restolino.framework.Api;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import java.io.IOException;
import java.util.stream.Collectors;

/**
 * Lists the available files.
 */
@Api
public class List {

	@GET
	public java.util.List<String> files(HttpServletRequest request, HttpServletResponse response) throws IOException {
		return Store.list().stream().map(path -> path.getFileName().toString()).collect(Collectors.toList());
	}
}
