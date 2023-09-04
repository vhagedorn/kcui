package me.vadim.ja.kc.render.impl.factory;

import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;

/**
 * @author vadim
 */
public final class PDFUtil {

	/**
	 * Supress nags from {@link COSDocument#finalize()} globally.
	 * @deprecated Do not use this method, since it masks actual bugs that should be fixed.
	 */
	@Deprecated
	public static void supressFinalizeWarnings() {
		java.util.logging.Logger.getLogger(COSDocument.class.getName()).setLevel(Level.SEVERE);
	}

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
	 * Export a {@link PDDocument PDF} to the disk.
	 * <p>The provided {@link PDDocument PDF} is <b>closed</b> after reading.
	 *
	 * @param doc  the source {@link PDDocument document}
	 * @param file the destination {@link File}; the file and necessary directories will be created if not already present
	 */
	public static void export(PDDocument doc, File file) {
		try (doc) {
			if (doc.getDocument().isClosed())
				throw new IllegalArgumentException("Document " + doc + " already closed!");

			if (!file.isFile()) {
				file.getParentFile().mkdirs();
				file.createNewFile();
			}
			if (!file.isFile())
				throw new IllegalArgumentException("File " + file + " is inaccessible.");

			doc.save(file);
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
	public static void closeSafely(@Nullable PDDocument... docs) {
		if (docs != null)
			for (PDDocument pdf : docs)
				if (pdf != null && !pdf.getDocument().isClosed())
					try {
						pdf.close();
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
	}

	/**
	 * Merges the provided {@link PDDocument PDFs} into a single {@link PDDocument PDF}.
	 * <p>The provided {@link PDDocument PDFs} may optionally be closed after reading.
	 *
	 * @param docs  an array of {@link PDDocument documents} to be read
	 * @param close whether or not to close the source documents after reading
	 * @param pages vararg of page numbers to take from each document (provide an empty array to copy all pages)
	 *
	 * @return the merged {@link PDDocument document}
	 */
	public static PDDocument mergePDFs(PDDocument[] docs, boolean close, int... pages) {
		PDFMergerUtility util = null;
		if (pages == null || pages.length == 0)
			util = new PDFMergerUtility();

		PDDocument output = new PDDocument();
		try {
			for (PDDocument pdf : docs)
				if (util != null)
					util.appendDocument(output, pdf);
				else
					for (int page : pages)
						output.importPage(pdf.getPage(page));

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			output.save(baos); // unfortuneately importPage does not in fact import the page and complains that the source document has been closed (AFTER the import!)

			if (close)
				closeSafely(docs);

			output.close();

			return PDDocument.load(baos.toByteArray());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Take certain pages from the provided {@link PDDocument PDF}.
	 * <p>The provided {@link PDDocument PDF} may optionally be closed after reading.
	 *
	 * @param doc   the {@link PDDocument document} to be read
	 * @param close whether or not to close the source documents after reading
	 * @param pages vararg of page numbers to take (may not be null or empty)
	 *
	 * @return the resulting {@link PDDocument document}
	 */
	public static PDDocument takePages(PDDocument doc, boolean close, int... pages) {
		if (pages == null || pages.length == 0)
			throw new IllegalArgumentException("pages");

		PDDocument output = new PDDocument();
		try {
			for (int page : pages)
				output.importPage(doc.getPage(page));

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			output.save(baos); // unfortuneately importPage does not in fact import the page and complains that the source document has been closed (AFTER the import!)

			if (close)
				closeSafely(doc);

			output.close();

			return PDDocument.load(baos.toByteArray());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	// stream-friendly overloads

	/**
	 * Merges the provided {@link PDDocument PDFs} into a single {@link PDDocument PDF}.
	 * <p>The provided {@link PDDocument PDFs} <b>are closed<b/> after reading.
	 *
	 * @param docs  an array of {@link PDDocument documents} to be read
	 * @param pages vararg of page numbers to take from each document (provide an empty array to copy all pages)
	 *
	 * @return the merged {@link PDDocument document}
	 */
	public static PDDocument mergePDFsClosing(PDDocument[] docs, int... pages) {
		return mergePDFs(docs, true, pages);
	}

	/**
	 * Merges the provided {@link PDDocument PDFs} into a single {@link PDDocument PDF}.
	 * <p>The provided {@link PDDocument PDFs} <b>are not closed<b/> after reading.
	 *
	 * @param docs  an array of {@link PDDocument documents} to be read
	 * @param pages vararg of page numbers to take from each document (provide an empty array to copy all pages)
	 *
	 * @return the merged {@link PDDocument document}
	 */
	public static PDDocument mergePDFsNotClosing(PDDocument[] docs, int... pages) {
		return mergePDFs(docs, false, pages);
	}

	/**
	 * Take certain pages from the provided {@link PDDocument PDF}.
	 * <p>The provided {@link PDDocument PDF} <b>are closed</b> after reading.
	 *
	 * @param doc   the {@link PDDocument document} to be read
	 * @param pages vararg of page numbers to take (may not be null or empty)
	 *
	 * @return the resulting {@link PDDocument document}
	 */
	public static PDDocument takePagesClosing(PDDocument doc, int... pages) {
		return takePages(doc, true, pages);
	}

	/**
	 * Take certain pages from the provided {@link PDDocument PDF}.
	 * <p>The provided {@link PDDocument PDF} <b>are not closed</b> after reading.
	 *
	 * @param doc   the {@link PDDocument document} to be read
	 * @param pages vararg of page numbers to take (may not be null or empty)
	 *
	 * @return the resulting {@link PDDocument document}
	 */
	public static PDDocument takePagesNotClosing(PDDocument doc, int... pages) {
		return takePages(doc, false, pages);
	}

}
