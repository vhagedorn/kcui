package me.vadim.ja.kc.card;

import me.vadim.ja.kc.wrapper.Kanji;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author vadim
 */
public class FlashcardPipeline {

	private final DiagramCreator  diag;
	private final PageServer      server;
	private final ConversionProxy proxy;

	public FlashcardPipeline(String kexec, int servport, String convurl) {
		try {
			this.diag   = new DiagramCreator(kexec, 200, true, 5, DiagramCreator.DOWN);
			this.server = new PageServer(8889);
			this.proxy  = new ConversionProxy(convurl);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public byte[] createFlashcardPDF(Kanji kanji) {
		char[]   chars = kanji.value.toCharArray();
		String[] imgs  = new String[chars.length];
		System.out.println(":: Generating stroke diagrams.");
		for (int i = 0; i < chars.length; i++)
			 imgs[i] = diag.strokeOrder(chars[i]);

		if (diag.isRTL()) {
			//reverse images for RTL consistency
			List<String> buf = Arrays.asList(imgs);
			Collections.reverse(buf);
			imgs = buf.toArray(String[]::new);
		}

		Generator gen = new Generator(kanji);

		System.out.println(":: Creating HTML pages.");
		server.setFront(gen.createFront());
		server.setBack(gen.createBack(imgs));

		System.out.println(":: Converting to PDFs.");
		String[]     urls  = server.getURLs();
		PDDocument[] pdfs  = new PDDocument[urls.length];
		byte[][]     blobs = proxy.requestConversionFor(urls);
		for (int i = 0; i < blobs.length; i++) {
			try {
				pdfs[i] = PDDocument.load(blobs[i]);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		System.out.println(":: Merging converted PDFs.");
		try (PDDocument output = new PDDocument()) {
			for (PDDocument pdf : pdfs)
				output.importPage(pdf.getPage(0));

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			output.save(baos);
			byte[] data = baos.toByteArray();
			output.close();
			for (PDDocument pdf : pdfs)
				pdf.close();
			//baos does not need to be closed
			return data;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		//todo: spin up electron
	}

}
