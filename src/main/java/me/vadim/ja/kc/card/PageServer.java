package me.vadim.ja.kc.card;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author vadim
 */
public class PageServer extends HttpAdapter implements ResourceAccess {

	public static final String FRONT = "/front.html";
	public static final String BACK  = "/back.html";

	private final HttpServer server;
	private final Handler    front, back;
	private final int                 port;
	private final Map<String, byte[]> resources;

	public void setFront(Document doc) {
		front.doc = doc;
	}

	public void setBack(Document doc) {
		back.doc = doc;
	}

	public PageServer(int port) throws IOException {
		this.resources = new HashMap<>() {{
			try (InputStream is = loadResource("doc/printing.css")) {
				put("/css/printing.css", is.readAllBytes());
			}
		}};
		front          = new Handler();
		back           = new Handler();
		this.port      = port;
		server         = HttpServer.create(new InetSocketAddress(port), 0);
		server.createContext(FRONT, front);
		server.createContext(BACK, back);
		server.createContext("/", this);//resources
		server.start();
	}

	public String getURL(String side) {
		return "http://localhost:" + port + side;
	}

	public String[] getURLs() {
		return new String[]{getURL(FRONT), getURL(BACK)};
	}

	@Override
	public void onRequest(HttpExchange exchange) throws IOException {
		if (!Objects.equals(exchange.getRequestMethod(), "GET")) {
			badMethod(exchange);
			return;
		}

		String req = exchange.getRequestURI().getPath();
		if (!resources.containsKey(req))
			req = req.substring(1);
		if (!resources.containsKey(req)) {
			respond(exchange, "text/plain", "Not found.", HttpURLConnection.HTTP_NOT_FOUND);
			return;
		}

		respond(exchange, URLConnection.guessContentTypeFromName(req), null, resources.get(req), HttpURLConnection.HTTP_OK);
	}

	private static void respond(HttpExchange exchange, String mime, String response, int code) throws IOException {
		respond(exchange, mime, StandardCharsets.UTF_8, response.getBytes(StandardCharsets.UTF_8), code);
	}

	private static void respond(HttpExchange exchange, String mime, Charset charset, byte[] response, int code) throws IOException {
		if (mime != null)
			exchange.getResponseHeaders().add("Content-Type", mime + (charset != null ? "; charset=" + charset.name().toLowerCase() : ""));
		exchange.sendResponseHeaders(code, response.length);
		OutputStream os = exchange.getResponseBody();
		os.write(response);
		os.close();
	}

	private static void badMethod(HttpExchange exchange) throws IOException {
		respond(exchange, "text/plain", "Bad request: method not allowed.", HttpURLConnection.HTTP_BAD_REQUEST);
	}

	private static class Handler extends HttpAdapter {
		Document doc;

		@Override
		public void onRequest(HttpExchange exchange) throws IOException {
			if (!Objects.equals(exchange.getRequestMethod(), "GET")) {
				badMethod(exchange);
				return;
			}

			respond(exchange, "text/html", doc.outerHtml(), HttpURLConnection.HTTP_OK);
		}
	}

}
