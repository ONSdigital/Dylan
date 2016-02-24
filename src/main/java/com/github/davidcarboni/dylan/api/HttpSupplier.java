package com.github.davidcarboni.dylan.api;

import com.github.davidcarboni.httpino.Http;

import java.util.function.Supplier;

/**
 * {@link Http} Supplier. Using this to get a new instance of the HTTP allows a mock or to be easily substituted in.
 */
public class HttpSupplier implements Supplier<Http> {

	@Override
	public Http get() {
		return new Http();
	}
}
