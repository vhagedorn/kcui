package me.vadim.ja.kc.render;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * @author vadim
 */
public class InMemoryFileServer {

	private final HttpServer server;
	private final int port;
	private final Map<String, Handler> handlers = new ConcurrentHashMap<>();

	public InMemoryFileServer(int port, boolean showIndex) throws IOException {
		this.port   = port;
		this.server = HttpServer.create(new InetSocketAddress(port), 0);
		this.server.start();

//		if (showIndex)
//			putResource("/index.html", new ServerResource("index.html", "How did you get here?\nWell, this is the static file server for the preview and PDF generation.\nLook in stdout for GET requests :)", "text/plain"));
////			putResource("/", new ServerResource("index.html", "null", "text/html") {
////				@Override
////				public byte[] getSnapshot() {
////					String url = "https://localhost:"+InMemoryFileServer.this.port;
////					StringBuilder html = new StringBuilder();
////					for (Handler value : handlers.values()) {
////						for (Map.Entry<String, ServerResource> entry : value.resources.entrySet()) {
////							String name = entry.getValue().getName();
////							//<a href="path">name</a>
////							html
////									.append("<p><a")
////									.append(" href=").append('"').append(url).append(value.path).append(name).append('"')
////									.append(">")
////									.append(entry.getValue().getName())
////									.append("</a></p>")
////									.append("\n");
////						}
////					}
////					System.out.println(html);
////					return html.toString().getBytes(StandardCharsets.UTF_8);
////				}
////			});
	}

	public String getURL() {
		return "http://localhost:" + port; // todo: hostname?
	}

	public static boolean dump = false;

	public String putResource(String path, ServerResource resource) {
		if (dump)
			try {
				File dumpDir = new File("dump", path);
				if (!dumpDir.isDirectory())
					dumpDir.mkdirs();
				if (dumpDir.isDirectory())
					Files.write(new File(dumpDir, resource.getName()).toPath(), resource.getSnapshot());
			} catch (IOException e) {
				e.printStackTrace();
			}

		String subpath = resource.getName();

		if (subpath.startsWith("/"))
			subpath = subpath.substring(0, subpath.length() - 1);

		if (!path.startsWith("/"))
			path = '/' + path;

		if (!path.endsWith("/"))
			path = path + '/';

		handlers.computeIfAbsent(path, Handler::new).resources.put(subpath, resource);

		return getURL() + path + subpath;
	}

	public void deleteResource(String path, String name) {
		Handler handler = handlers.computeIfAbsent(path, Handler::new);
		String  subpath = name;

		if (!subpath.startsWith("/"))
			subpath = '/' + subpath;

		handler.resources.remove(subpath);
		handler.resources.remove(name); // just in case
	}

	public void deletePath(String path) {
		handlers.remove(path);
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

	private static void notFound(HttpExchange exchange) throws IOException {
		respond(exchange, "text/plain", "Not found.", HttpURLConnection.HTTP_NOT_FOUND);
	}

	private static void badMethod(HttpExchange exchange) throws IOException {
		respond(exchange, "text/plain", "Bad request: method not allowed.", HttpURLConnection.HTTP_BAD_REQUEST);
	}

	private class Handler extends HttpAdapter {

		final String path;
		final HttpContext context;
		final Map<String, ServerResource> resources = new HashMap<>();

		Handler(String path) {
			this.path    = path;
			this.context = server.createContext(path, this);
		}

		@Override
		public void onRequest(HttpExchange exchange) throws IOException {
			if (!Objects.equals(exchange.getRequestMethod(), "GET")) {
				badMethod(exchange);
				return;
			}

			String req = exchange.getRequestURI().getPath().replaceFirst(Pattern.quote(path), "");
			if (!resources.containsKey(req))
				req = req.substring(1);
			if (!resources.containsKey(req)) {
				notFound(exchange);
				return;
			}
			ServerResource resource = resources.get(req);

			respond(exchange,
					resource.getMimeType() == null ? URLConnection.guessContentTypeFromName(req) : resource.getMimeType(),
					resource.getEncoding(),
					resource.getSnapshot(),
					HttpURLConnection.HTTP_OK);
		}

	}

	public void close() {
		server.stop(5);
	}

}
