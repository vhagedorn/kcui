package me.vadim.ja.kc.render.factory;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import me.vadim.ja.kc.db.impl.blob.BlobCache;
import me.vadim.ja.kc.render.ConversionService;
import me.vadim.ja.kc.render.PrintOptions;
import me.vadim.ja.kc.render.img.DiagramCreator;
import me.vadim.ja.kc.render.img.StrokeOrderRegistry;
import me.vadim.ja.kc.wrapper.Kanji;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.jsoup.nodes.Document;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * @author vadim
 */
public class FlashcardPipeline {

	private final StrokeOrderRegistry sor;
	private final ConversionService   svc;

	public FlashcardPipeline(DiagramCreator diag, ConversionService svc) {
		this.sor = new StrokeOrderRegistry(diag, new BlobCache());
		this.svc = svc;
	}

	public void cacheStrokeDiagrams(Kanji partial) {
		sor.submitQuery(partial, StrokeOrderRegistry.DEFAULT_OPTS);
	}

	private final ExecutorService worker = Executors.newCachedThreadPool(new ThreadFactoryBuilder().setDaemon(true).setNameFormat("Card pipeline worker %d").build());

	public byte[] createFlashcardPDF(Kanji kanji, int renderOpts) {
		System.out.println(":: Gathering stroke diagrams.");
		String[] imgs = sor.queryDiagrams(kanji, renderOpts);

		System.out.println(":: Creating HTML pages.");
		Generator gen   = new Generator(kanji);
		Document  front = gen.createFront();
		Document  back  = gen.createBack(imgs);

		System.out.println(":: Converting to PDFs.");
		PrintOptions opts = PrintOptions.index();
		PDDocument[] pdfs = {
				svc.createPDF(front, opts),
				svc.createPDF(back, opts)
		};

		System.out.println(":: Merging converted PDFs.");
		PDDocument merged = PDFUtil.mergePDFs(pdfs, 0);
		byte[]     result = PDFUtil.export(merged);
		PDFUtil.closeSafely(pdfs);
		return result;
	}

	public CompletableFuture<File> queueFlashcardGeneration(Kanji kanji, File target, int renderOpts) {
		return sor.submitQuery(kanji, renderOpts).thenComposeAsync(imgs -> {
			Generator gen   = new Generator(kanji);
			Document  front = gen.createFront();
			Document  back  = gen.createBack(imgs);

			PrintOptions opts = PrintOptions.index();
			List<CompletableFuture<PDDocument>> jobs =
					Arrays.asList(
							svc.submitJob(front, opts),
							svc.submitJob(back, opts)
								 );
			return CompletableFuture.allOf(jobs.toArray(CompletableFuture[]::new))
									.thenApplyAsync(x -> jobs.stream().map(CompletableFuture::join).collect(Collectors.toList()), worker)
									.thenApplyAsync(list -> {
										PDDocument[] pdfs   = list.toArray(PDDocument[]::new);
										PDDocument   merged = PDFUtil.mergePDFs(pdfs, 0);
										PDFUtil.export(merged, target);
										PDFUtil.closeSafely(pdfs);
										return target;
									}, worker);
		}, worker);
	}

}
