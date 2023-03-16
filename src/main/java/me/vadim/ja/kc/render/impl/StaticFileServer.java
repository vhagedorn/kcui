package me.vadim.ja.kc.render.impl;

import me.vadim.ja.kc.render.InMemoryFileServer;
import me.vadim.ja.kc.render.ServerResource;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * @author vadim
 */
public class StaticFileServer extends InMemoryFileServer {

	private final String uploadPath;

	public StaticFileServer(int port, String uploadPath, ServerResourceIdentifier... identifiers) throws IOException {
		super(port);
		this.uploadPath = uploadPath;
		for (ServerResourceIdentifier sri : identifiers) {
			try (InputStream is = loadResource(sri.resourceURL)) {
				putResource(sri.path, new ServerResource(sri.name, is.readAllBytes(), sri.mimeType, sri.charset));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public void resetSession(){
		super.deletePath(uploadPath);
	}

	public String uploadDocument(Document html) {
		return putResource(uploadPath, new ServerResource(UUID.randomUUID() + ".html", html.outerHtml(), "text/html"));
	}

}
