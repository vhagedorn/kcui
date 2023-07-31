package me.vadim.ja.kc.render.impl.electron;

import me.vadim.ja.kc.render.DocConverters;
import me.vadim.ja.kc.render.impl.PDFConversionService;
import me.vadim.ja.kc.render.impl.PrintOptions;
import me.vadim.ja.kc.render.impl.StaticFileServer;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

/**
 * @author vadim
 */
public class ElectronPDFConverter implements PDFConversionService {

	private final ElectronPDFProxy proxy;
	private final StaticFileServer server;
	private final ExecutorService worker;

	public ElectronPDFConverter(int port, String electronConvertURL, ExecutorService worker) {
		this.proxy  = new ElectronPDFProxy(electronConvertURL);
		this.worker = worker;
		try {
			this.server = new StaticFileServer(port, true, "/uploads");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		DocConverters.putPrintingCss(server);
	}

	@Override
	public PDDocument createPDF(Document html, PrintOptions options) {
		//:kekA: options ignored
		String url = server.uploadDocument(html);
		byte[] pdf = proxy.requestConversionFor(url)[0];
		try {
			return PDDocument.load(pdf);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	//hehehehaw
	@Override
	public CompletableFuture<PDDocument> printJob(Document html, PrintOptions options) {
		return CompletableFuture.supplyAsync(() -> createPDF(html, options), worker);
	}

	@Override
	public void close() {
		worker.shutdown();
		server.close();
	}

}
