package me.vadim.ja.kc.render.impl;

import me.vadim.ja.kc.render.InMemoryFileServer;
import me.vadim.ja.kc.render.ServerResource;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.atomic.AtomicLong;

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

	private final AtomicLong id = new AtomicLong(0);
	private final DateFormat df = new SimpleDateFormat("MM.yy-HH.mm.ss");

	private String uid() {
		return df.format(Calendar.getInstance().getTime()) + "_"+id.getAndIncrement()+".html";
	}

	public String uploadDocument(Document html) {
		return putResource(uploadPath, new ServerResource(uid(), html.outerHtml(), "text/html"));
	}

}
