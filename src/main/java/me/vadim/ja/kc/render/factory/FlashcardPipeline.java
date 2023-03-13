package me.vadim.ja.kc.render.factory;

import me.vadim.ja.kc.render.ConversionService;
import me.vadim.ja.kc.render.PrintOptions;
import me.vadim.ja.kc.wrapper.Kanji;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.jsoup.nodes.Document;

/**
 * @author vadim
 */
public class FlashcardPipeline {

	private final StrokeOrderRegistry sor;
	private final ConversionService   svc;

	public FlashcardPipeline(DiagramCreator diag, ConversionService svc) {
		this.sor = new StrokeOrderRegistry(diag);
		this.svc  = svc;
	}

	public byte[] createFlashcardPDF(Kanji kanji) {
		System.out.println(":: Gathering stroke diagrams.");
		String[] imgs = sor.queryDiagrams(kanji);

		System.out.println(":: Creating HTML pages.");
		Generator gen = new Generator(kanji);
		Document front = gen.createFront();
		Document back  = gen.createBack(imgs);

		System.out.println(":: Converting to PDFs.");
		PrintOptions opts = PrintOptions.letter();
		PDDocument[] pdfs = {
				svc.createPDF(front, opts),
				svc.createPDF(back, opts)
		};

		System.out.println(":: Merging converted PDFs.");
		PDDocument merged = PDFUtil.mergePDFs(pdfs, 0);
		byte[] result = PDFUtil.export(merged);
		PDFUtil.closeSafely(pdfs);
		return result;
	}
}
