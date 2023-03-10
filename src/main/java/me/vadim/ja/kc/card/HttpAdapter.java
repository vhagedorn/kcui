package me.vadim.ja.kc.card;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

/**
 * {@link HttpHandler} adapter which logs each request to the console.
 *
 * @author vadim
 */
public abstract class HttpAdapter implements HttpHandler {

	@Override
	public final void handle(HttpExchange exchange) throws IOException {
		System.out.println("<- " + exchange.getRequestMethod() + " " + exchange.getRequestURI());
		onRequest(exchange);
	}

	protected abstract void onRequest(HttpExchange exchange) throws IOException;
}
