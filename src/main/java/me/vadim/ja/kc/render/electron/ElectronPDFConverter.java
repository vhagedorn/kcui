package me.vadim.ja.kc.render.electron;

import me.vadim.ja.kc.ResourceAccess;
import me.vadim.ja.kc.render.ConversionService;
import me.vadim.ja.kc.render.InMemoryFileServer;
import me.vadim.ja.kc.render.PrintOptions;
import me.vadim.ja.kc.render.ServerResource;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * @author vadim
 */
public class ElectronPDFConverter implements ConversionService, ResourceAccess {

	private final ElectronPDFProxy   proxy;
	private final InMemoryFileServer server;

	public ElectronPDFConverter(int port, String electronConvertURL) {
		try (InputStream is = loadResource("doc/printing.css")) {
			this.proxy  = new ElectronPDFProxy(electronConvertURL);
			this.server = new InMemoryFileServer(port);
			server.putResource("/css", new ServerResource("printing.css", is.readAllBytes(), "text/css", StandardCharsets.UTF_8));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public PDDocument createPDF(Document html, PrintOptions options) {
		String name = UUID.randomUUID() + ".html";
		String url  = server.putResource("/html", new ServerResource(name, html.outerHtml(), "text/html"));
		byte[] pdf = proxy.requestConversionFor(url)[0];
		try {
			return PDDocument.load(pdf);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	//hehehehaw
	@Override
	public CompletableFuture<PDDocument> submitJob(Document html, PrintOptions options) {
		return CompletableFuture.supplyAsync(() -> createPDF(html, options));
	}

}
