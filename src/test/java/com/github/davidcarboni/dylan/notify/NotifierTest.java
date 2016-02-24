package com.github.davidcarboni.dylan.notify;

import com.github.davidcarboni.dylan.api.HttpSupplier;
import com.github.davidcarboni.httpino.Endpoint;
import com.github.davidcarboni.httpino.Http;
import com.github.davidcarboni.httpino.Response;
import org.apache.http.StatusLine;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.nio.file.Path;

import static javax.ws.rs.core.Response.Status.OK;
import static javax.ws.rs.core.Response.Status.SERVICE_UNAVAILABLE;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test the Recipient Notifier functionality.
 */
public class NotifierTest {

	@Mock
	private HttpSupplier httpSupplier;

	@Mock
	private Http http;

	@Mock
	private Path path;

	@Mock
	private Path nestedPath;

	@Mock
	private StatusLine statusLine;

	private Response<String> response;
	private String nestedFileName = "nestedPathName";
	private String responseBody = "There's a lady who's sure all that glitters is gold, And she's buying a stairway to heaven.";
	private String reasonPhrase = "You must find the Jade Monkey before the next full moon.";

	@Before
	public void setup() throws Exception {
		MockitoAnnotations.initMocks(this);

		when(path.getFileName())
				.thenReturn(nestedPath);
		when(nestedPath.toString())
				.thenReturn(nestedFileName);

		response = new Response<>(statusLine, responseBody);
	}

	@Test
	public void shouldSendNotificationSuccessfully() throws Exception {
		when(httpSupplier.get())
				.thenReturn(http);
		when(http.postJson(any(Endpoint.class), eq(nestedFileName), eq(String.class)))
				.thenReturn(response);
		when(statusLine.getStatusCode())
				.thenReturn(OK.getStatusCode());

		Notifier.notify(path, httpSupplier);
		verify(httpSupplier, times(1)).get();
		verify(http, times(1)).postJson(any(Endpoint.class), eq(nestedFileName), eq(String.class));
	}

	@Test (expected = IOException.class)
	public void shouldThrowIOExceptionIfStatusCodeIsNotOK() throws Exception {
		when(httpSupplier.get())
				.thenReturn(http);
		when(http.postJson(any(Endpoint.class), eq(nestedFileName), eq(String.class)))
				.thenReturn(response);
		when(statusLine.getStatusCode())
				.thenReturn(SERVICE_UNAVAILABLE.getStatusCode());
		when(statusLine.getReasonPhrase())
				.thenReturn(reasonPhrase);

		try {
			Notifier.notify(path, httpSupplier);
		} catch (IOException ex) {
			assertThat("Exception message not as expected.", ex.getMessage(), equalTo(reasonPhrase));
			verify(httpSupplier, times(1)).get();
			verify(http, times(1)).postJson(any(Endpoint.class), eq(nestedFileName), eq(String.class));
			throw ex;
		}
	}

	@Test (expected = IOException.class)
	public void shouldPropagateIOExceptionFromHttpPost() throws Exception {
		when(httpSupplier.get())
				.thenReturn(http);
		when(http.postJson(any(Endpoint.class), eq(nestedFileName), eq(String.class)))
				.thenThrow(new IOException());
		try {
			Notifier.notify(path, httpSupplier);
		} catch (IOException ex) {
			verify(httpSupplier, times(1)).get();
			verify(http, times(1)).postJson(any(Endpoint.class), eq(nestedFileName), eq(String.class));
			throw ex;
		}
	}
}
