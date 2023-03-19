package me.vadim.ja.kc.render.impl.puppeteer;

import com.ruiyun.jvppeteer.core.Puppeteer;
import com.ruiyun.jvppeteer.core.browser.Browser;
import com.ruiyun.jvppeteer.core.browser.BrowserFetcher;
import com.ruiyun.jvppeteer.core.page.Page;
import com.ruiyun.jvppeteer.options.LaunchOptions;
import com.ruiyun.jvppeteer.options.LaunchOptionsBuilder;
import com.ruiyun.jvppeteer.options.ScreenshotOptions;
import com.ruiyun.jvppeteer.options.Viewport;
import me.vadim.ja.kc.render.DocConverters;
import me.vadim.ja.kc.render.PageSize;
import me.vadim.ja.kc.render.impl.PreviewConversionService;
import me.vadim.ja.kc.render.impl.PrintOptions;
import me.vadim.ja.kc.render.impl.StaticFileServer;
import org.jsoup.nodes.Document;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

/**
 * @author vadim
 */
public class JvppetteerPreviewConverter implements PreviewConversionService {


	private final StaticFileServer server;
	private final ExecutorService worker;
	private final Browser browser;

	// oh, how much I hate checked exceptions
	public JvppetteerPreviewConverter(int port, ExecutorService worker) {
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
	public BufferedImage createPreview(Document html, PrintOptions options) {
		try {
			File tmp = File.createTempFile("puppeteer", "png");
			Page    page    = browser.newPage();
			page.goTo(server.uploadDocument(html), true);

			Viewport vp = new Viewport();
			PageSize ps = options.getSize().withUnit("px");
			vp.setWidth((int) ps.width());
			vp.setHeight((int) ps.height());
			page.setViewport(vp);

			ScreenshotOptions ssOpts = new ScreenshotOptions();
			ssOpts.setType("png");
			ssOpts.setFullPage(true);
			ssOpts.setPath(tmp.getAbsolutePath());

			page.screenshot(ssOpts);
			page.close();
			tmp.deleteOnExit();

			return ImageIO.read(tmp);
		}catch (IOException e){
			throw new RuntimeException(e);
		} catch (InterruptedException e){
			return null;
		}
	}

	@Override
	public CompletableFuture<BufferedImage> screenshotJob(Document html, PrintOptions options) {
		return CompletableFuture.supplyAsync(() -> createPreview(html ,options), worker);
	}

	@Override
	public void close() {
		worker.shutdown();
		server.close();
		browser.close();
	}
}