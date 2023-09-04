package me.vadim.ja.kc.render.impl.electron;

import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

/**
 * @author vadim
 */
public class ElectronPDFProxy {

	private final String url;

	public ElectronPDFProxy(String url) {
		this.url = url;
	}

	public String getURL() {
		return url;
	}

	public byte[][] requestConversionFor(String... urls) {
		byte[][] res = new byte[urls.length][];
		for (int i = 0; i < urls.length; i++) {
			try {
				//create connection
				URL               url  = new URL(this.url);
				URLConnection     con  = url.openConnection();
				HttpURLConnection http = (HttpURLConnection) con;
				http.setRequestMethod("POST");
				http.setDoOutput(true);

				//prepare request
				JsonObject payload = new JsonObject();
				payload.addProperty("url", urls[i]);
				byte[] request = payload.toString().getBytes(StandardCharsets.UTF_8);
				int    length  = request.length;

				//send body
				http.setFixedLengthStreamingMode(length);
				http.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

				System.out.println("-> " + http.getRequestMethod() + " " + http.getURL());
				http.connect();
				try (OutputStream os = http.getOutputStream()) {
					os.write(request);
				}

				//read response
				try (InputStream is = http.getInputStream()) {
					res[i] = is.readAllBytes();
				}
			} catch (IOException e) {
//				res[i] = new byte[0];
				throw new RuntimeException(e);//why do checked exceptions even exit...
			}
		}
		return res;
	}

}
