package me.vadim.ja.kc.render.impl.puppeteer;

import com.ruiyun.jvppeteer.core.Puppeteer;
import com.ruiyun.jvppeteer.core.browser.Browser;
import com.ruiyun.jvppeteer.core.browser.BrowserFetcher;
import com.ruiyun.jvppeteer.core.page.Page;
import com.ruiyun.jvppeteer.options.LaunchOptions;
import com.ruiyun.jvppeteer.options.LaunchOptionsBuilder;
import com.ruiyun.jvppeteer.options.PDFOptions;
import com.ruiyun.jvppeteer.protocol.DOM.Margin;
import me.vadim.ja.kc.render.DocConverters;
import me.vadim.ja.kc.render.PageSize;
import me.vadim.ja.kc.render.impl.PDFConversionService;
import me.vadim.ja.kc.render.impl.PrintOptions;
import me.vadim.ja.kc.render.impl.StaticFileServer;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

/**
 * @author vadim
 */
public class JvppetteerPDFConverter implements PDFConversionService {


	private static Margin margins(String css){
		Margin margin = new Margin();
		margin.setLeft(css);
		margin.setRight(css);
		margin.setTop(css);
		margin.setBottom(css);
		return margin;
	}

	private final StaticFileServer server;
	private final ExecutorService worker;
	private final Browser browser;

	// oh, how much I hate checked exceptions
	public JvppetteerPDFConverter(int port, ExecutorService worker) {
		this.worker = worker;
		try {
			this.server = new StaticFileServer(port, "/v2");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		DocConverters.putPrintingCss(server);

		try {
			BrowserFetcher.downloadIfNotExist(null);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		} catch (IOException | ExecutionException e) {
			throw new RuntimeException(e);
		}

		LaunchOptions launchOptions = new LaunchOptionsBuilder()
				.withArgs(Arrays.asList("--no-sandbox", "--disable-gpu", "--disable-setuid-sandbox"))
				.withHeadless(true).build();
		try {
			browser = Puppeteer.launch(launchOptions);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public PDDocument createPDF(Document html, PrintOptions options) {
		try {
			File tmp = File.createTempFile("puppeteer", "pdf");
			Page    page    = browser.newPage();
			page.goTo(server.uploadDocument(html), true);

			PDFOptions pdfOpts = new PDFOptions();
			PageSize ps = options.getSize().withUnit("in");
			pdfOpts.setWidth(ps.width() + "in");
			pdfOpts.setHeight(ps.height() + "in");
			pdfOpts.setDisplayHeaderFooter(options.printHeadersAndFooters());
			pdfOpts.setMargin(options.getMargins().toMargin());
			pdfOpts.setLandscape(options.isLandscape());
			pdfOpts.setPreferCSSPageSize(false);
			pdfOpts.setPath(tmp.getAbsolutePath());

			page.pdf(pdfOpts); // todo: this returns a byte array; no need for temp file
			page.close();
			tmp.deleteOnExit();

			return PDDocument.load(tmp);
		}catch (IOException e){
			throw new RuntimeException(e);
		} catch (InterruptedException e){
			return null;
		}
	}

	@Override
	public CompletableFuture<PDDocument> printJob(Document html, PrintOptions options) {
		return CompletableFuture.supplyAsync(() -> createPDF(html ,options), worker);
	}

	@Override
	public void close() {
		worker.shutdown();
		server.close();
		browser.close();
	}
}