package me.vadim.ja.kc.render;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.jsoup.nodes.Document;

import java.util.concurrent.CompletableFuture;

/**
 * Defines a service that can convert {@link Document HTML documents} to {@link PDDocument PDFs}.
 * @author vadim
 */
public interface ConversionService {

	/**
	 * Convert an {@link Document HTML document} to a {@link PDDocument PDF}.
	 * <p><b>This is a <i>blocking</i> method.</b>
	 * @param html the {@link Document HTML document}
	 * @param options {@link PrintOptions settings} pertaining to the print job
	 * @return the rendered {@link PDDocument PDF}
	 */
	PDDocument createPDF(Document html, PrintOptions options);

	/**
	 * Convert an {@link Document HTML document} to a {@link PDDocument PDF}.
	 * <p><b>This is an <i>asynchronous</i> method.</b>
	 * @param html the {@link Document HTML document}
	 * @param options {@link PrintOptions settings} pertaining to the print job
	 * @return a {@link CompletableFuture promise} that will return the rendered {@link PDDocument PDF}
	 */
	CompletableFuture<PDDocument> submitJob(Document html, PrintOptions options);

}
