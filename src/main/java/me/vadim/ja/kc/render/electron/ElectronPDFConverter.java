package me.vadim.ja.kc.render.electron;

import me.vadim.ja.kc.ResourceAccess;
import me.vadim.ja.kc.render.ConversionService;
import me.vadim.ja.kc.render.PrintOptions;
import me.vadim.ja.kc.render.impl.ServerResourceIdentifier;
import me.vadim.ja.kc.render.impl.StaticFileServer;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

/**
 * @author vadim
 */
public class ElectronPDFConverter implements ConversionService, ResourceAccess {

	private final ElectronPDFProxy proxy;
	private final StaticFileServer server;
	private final ExecutorService worker;

	public ElectronPDFConverter(int port, String electronConvertURL, ExecutorService worker) {
		this.proxy = new ElectronPDFProxy(electronConvertURL);
		this.worker = worker;
		try {
			this.server = new StaticFileServer(port, "/uploads",
											   new ServerResourceIdentifier("/css", "printing.css", "text/css", "doc/printing.css"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public PDDocument createPDF(Document html, PrintOptions options) {
		//:kekA: options ignored
		String url  = server.uploadDocument(html);
		byte[] pdf  = proxy.requestConversionFor(url)[0];
		try {
			return PDDocument.load(pdf);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	//hehehehaw
	@Override
	public CompletableFuture<PDDocument> submitJob(Document html, PrintOptions options) {
		return CompletableFuture.supplyAsync(() -> createPDF(html, options), worker);
	}

}
