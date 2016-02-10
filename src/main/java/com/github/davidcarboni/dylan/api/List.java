package com.github.davidcarboni.dylan.api;

import com.github.davidcarboni.restolino.framework.Api;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import java.util.ArrayList;

/**
 * Lists the available files.
 */
@Api
public class List {

    @GET
    public java.util.List<String> files(HttpServletRequest request, HttpServletResponse response) {
        //String filename Path.newInstance(request).lastSegment();
        return new ArrayList<>();
    }
}
