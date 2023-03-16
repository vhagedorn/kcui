package me.vadim.ja.kc.render.puppeteer;

import com.ruiyun.jvppeteer.core.Puppeteer;
import com.ruiyun.jvppeteer.core.browser.Browser;
import com.ruiyun.jvppeteer.core.browser.BrowserFetcher;
import com.ruiyun.jvppeteer.core.page.Page;
import com.ruiyun.jvppeteer.options.LaunchOptions;
import com.ruiyun.jvppeteer.options.LaunchOptionsBuilder;
import com.ruiyun.jvppeteer.options.PDFOptions;
import com.ruiyun.jvppeteer.protocol.DOM.Margin;
import me.vadim.ja.kc.render.ConversionService;
import me.vadim.ja.kc.render.PrintOptions;
import me.vadim.ja.kc.render.impl.ServerResourceIdentifier;
import me.vadim.ja.kc.render.impl.StaticFileServer;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

/**
 * @author vadim
 */
public class JvppetteerPDFConverter implements ConversionService {


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

	public JvppetteerPDFConverter(int port, ExecutorService worker) {
		this.worker = worker;
		try {
			this.server = new StaticFileServer(port, "/v2", new ServerResourceIdentifier("/css", "printing.css", "text/css", "doc/printing.css"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public PDDocument createPDF(Document html, PrintOptions options) {
		try {
			File tmp = File.createTempFile("puppeteer", "pdf");

			BrowserFetcher.downloadIfNotExist(null);
			ArrayList<String> arrayList = new ArrayList<>();
			//生成pdf必须在无厘头模式下才能生效
			LaunchOptions launchOptions = new LaunchOptionsBuilder().withArgs(arrayList).withHeadless(true).build();
			arrayList.add("--no-sandbox");
			arrayList.add("--disable-gpu");
			arrayList.add("--disable-setuid-sandbox");

			Browser browser = Puppeteer.launch(launchOptions);
			Page    page    = browser.newPage();
			page.setContent(html.outerHtml());
			page.goTo(server.uploadDocument(html), true);

			PDFOptions pdfOptions = new PDFOptions();
			pdfOptions.setWidth(options.getSize().width() + "in");
			pdfOptions.setHeight(options.getSize().height() + "in");
			pdfOptions.setDisplayHeaderFooter(options.printHeadersAndFooters());
			pdfOptions.setMargin(options.getMargins().toMargin());
			pdfOptions.setLandscape(options.isLandscape());
			pdfOptions.setPreferCSSPageSize(false);
			pdfOptions.setPath(tmp.getAbsolutePath());

			page.pdf(pdfOptions);
			page.close();
			browser.close();
			tmp.deleteOnExit();

			return PDDocument.load(tmp);
		}catch (IOException | ExecutionException e){
			throw new RuntimeException(e);
		} catch (InterruptedException e){
			return null;
		}
	}

	@Override
	public CompletableFuture<PDDocument> submitJob(Document html, PrintOptions options) {
		return CompletableFuture.supplyAsync(() -> createPDF(html ,options), worker);
	}
}