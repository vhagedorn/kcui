package me.vadim.ja.kc.render.factory;

import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author vadim
 */
public final class PDFUtil {

	/**
	 * Export a {@link PDDocument PDF} into a {@code byte[]} array.
	 * <p>The provided {@link PDDocument PDF} is <b>closed</b> after reading.
	 *
	 * @param doc the source {@link PDDocument document}
	 *
	 * @return the {@link PDDocument#save(OutputStream) saved} {@link PDDocument document}
	 */
	public static byte[] export(PDDocument doc) {
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			doc.save(baos);
			byte[] data = baos.toByteArray();
			doc.close();
			return data;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Safely closes all {@link PDDocument PDFs} provided.
	 * This method wraps and rethrows any {@link IOException}s as {@link RuntimeException}s.
	 * It also disregards any {@link PDDocument documents} that have already been {@link PDDocument#close() closed}.
	 *
	 * @param docs array of {@link PDDocument PDFs}
	 */
	public static void closeSafely(PDDocument... docs) {
		for (PDDocument pdf : docs)
			if (!pdf.getDocument().isClosed())
				try {
					pdf.close();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
	}

	/**
	 * Merges the provided {@link PDDocument PDFs} into a single {@link PDDocument PDF}.
	 * <p>The provided {@link PDDocument PDFs} are <b>not closed</b> after reading.
	 *
	 * @param docs  an array of {@link PDDocument documents} to be read
	 * @param pages vararg of page numbers to take from each page (provide an empty array to copy all pages)
	 *
	 * @return the merged {@link PDDocument document}
	 */
	public static PDDocument mergePDFs(PDDocument[] docs, int... pages) {
		PDFMergerUtility util = null;
		if (pages.length == 0)
			util = new PDFMergerUtility();

		try (PDDocument output = new PDDocument()) {
			for (PDDocument pdf : docs)
				if (util != null)
					util.appendDocument(output, pdf);
				else
					for (int page : pages)
						output.importPage(pdf.getPage(page));

			return output;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
