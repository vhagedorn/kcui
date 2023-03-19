package me.vadim.ja.kc.render.impl.factory;

import me.vadim.ja.kc.KanjiCardUI;
import me.vadim.ja.kc.db.impl.blob.BlobCache;
import me.vadim.ja.kc.render.impl.PDFConversionService;
import me.vadim.ja.kc.render.impl.PreviewConversionService;
import me.vadim.ja.kc.render.impl.PrintOptions;
import me.vadim.ja.kc.render.impl.img.DiagramCreator;
import me.vadim.ja.kc.render.impl.img.StrokeOrderRegistry;
import me.vadim.ja.kc.wrapper.Kanji;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.jsoup.nodes.Document;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

/**
 * @author vadim
 */
public class FlashcardPipeline {

	private final StrokeOrderRegistry                     sor;
	private final PDFConversionService pdf;
	private final PreviewConversionService png;

	public FlashcardPipeline(DiagramCreator diag, PDFConversionService pdf, PreviewConversionService png) {
		this.sor = new StrokeOrderRegistry(diag, new BlobCache());
		this.pdf = pdf;
		this.png = png;
	}

	public void cacheStrokeDiagrams(Kanji partial) {
		sor.submitQuery(partial, StrokeOrderRegistry.DEFAULT_OPTS);
	}

	private final ExecutorService worker = KanjiCardUI.threadPool("Card pipeline worker %d");

	@SuppressWarnings("resource")
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
				pdf.createPDF(front, opts),
				pdf.createPDF(back, opts)
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
							pdf.submitJob(front, opts),
							pdf.submitJob(back, opts)
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

	public BufferedImage[] createFlashcardPreview(Kanji kanji, int renderOpts) {
		System.out.println(":: Gathering stroke diagrams.");
		String[] imgs = sor.queryDiagrams(kanji, renderOpts);

		System.out.println(":: Creating HTML pages.");
		Generator gen   = new Generator(kanji);
		Document  front = gen.createFront();
		Document  back  = gen.createBack(imgs);

		System.out.println(":: Generating previews.");
		PrintOptions opts = PrintOptions.index();
		return new BufferedImage[]{
				png.createPreview(front, opts),
				png.createPreview(back, opts)
		};
	}

	public CompletableFuture<BufferedImage[]> queueFlashcardPreview(Kanji kanji, int renderOpts) {
		return sor.submitQuery(kanji, renderOpts).thenComposeAsync(imgs -> {
			Generator gen   = new Generator(kanji);
			Document  front = gen.createFront();
			Document  back  = gen.createBack(imgs);

			PrintOptions opts = PrintOptions.index();
			List<CompletableFuture<BufferedImage>> jobs =
					Arrays.asList(
							png.submitJob(front, opts),
							png.submitJob(back, opts));

			return CompletableFuture.allOf(jobs.toArray(CompletableFuture[]::new))
									.thenApplyAsync(x -> jobs.stream().map(CompletableFuture::join).toArray(BufferedImage[]::new), worker);
		}, worker);
	}

	public void close() {
		worker.shutdown();
		pdf.close();
		png.close();
	}

}
