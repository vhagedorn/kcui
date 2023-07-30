package me.vadim.ja.kc.render.impl;

import me.vadim.ja.kc.render.ConversionService;
import me.vadim.ja.kc.render.ConvertOptions;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.jsoup.nodes.Document;

import java.util.concurrent.CompletableFuture;

/**
 * @author vadim
 */
public interface PDFConversionService extends ConversionService<Document, PDDocument> {

	@Override
	default PDDocument convert(Document input, ConvertOptions options) {
		if (!(options instanceof PrintOptions))
			throw new IllegalArgumentException("Options must be instance of PrintOptions. Got: " + options.getClass().getSimpleName());

		return createPDF(input, (PrintOptions) options);
	}

	@Override
	default CompletableFuture<PDDocument> submitJob(Document input, ConvertOptions options) {
		if (!(options instanceof PrintOptions))
			throw new IllegalArgumentException("Options must be instance of PrintOptions. Got: " + options.getClass().getSimpleName());

		return printJob(input, (PrintOptions) options);
	}

	/**
	 * Convert an {@link Document HTML document} to a {@link PDDocument PDF}.
	 * <p><b>This is a <i>blocking</i> method.</b>
	 *
	 * @param html    the {@link Document HTML document}
	 * @param options {@link PrintOptions settings} pertaining to the print job
	 *
	 * @return the rendered {@link PDDocument PDF}
	 */
	PDDocument createPDF(Document html, PrintOptions options);

	/**
	 * Convert an {@link Document HTML document} to a {@link PDDocument PDF}.
	 * <p><b>This is an <i>asynchronous</i> method.</b>
	 *
	 * @param html    the {@link Document HTML document}
	 * @param options {@link PrintOptions settings} pertaining to the print job
	 *
	 * @return a {@link CompletableFuture promise} that will return the rendered {@link PDDocument PDF}
	 */
	CompletableFuture<PDDocument> printJob(Document html, PrintOptions options);

}
