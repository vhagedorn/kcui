package me.vadim.ja.kc.render.impl;

import me.vadim.ja.kc.render.ConversionService;
import me.vadim.ja.kc.render.ConvertOptions;
import org.jsoup.nodes.Document;

import java.awt.image.BufferedImage;
import java.util.concurrent.CompletableFuture;

/**
 * @author vadim
 */
public interface PreviewConversionService extends ConversionService<Document, BufferedImage> {

	@Override
	default BufferedImage convert(Document input, ConvertOptions options) {
		if(!(options instanceof PrintOptions))
			throw new IllegalArgumentException("Options must be instance of PrintOptions. Got: "+options.getClass().getSimpleName());

		return createPreview(input, (PrintOptions) options);
	}

	@Override
	default CompletableFuture<BufferedImage> submitJob(Document input, ConvertOptions options) {
		if(!(options instanceof PrintOptions))
			throw new IllegalArgumentException("Options must be instance of PrintOptions. Got: "+options.getClass().getSimpleName());

		return screenshotJob(input, (PrintOptions) options);
	}

	/**
	 * Generate a print {@link BufferedImage preview} for a given {@link Document HTML document}.
	 * <p><b>This is a <i>blocking</i> method.</b>
	 * @param html the {@link Document HTML document}
	 * @param options {@link PrintOptions settings} pertaining to the print job
	 * @return the rendered {@link BufferedImage preview}
	 */
	BufferedImage createPreview(Document html, PrintOptions options);

	/**
	 * Generate a print {@link BufferedImage preview} for a given {@link Document HTML document}.
	 * <p><b>This is an <i>asynchronous</i> method.</b>
	 * @param html the {@link Document HTML document}
	 * @param options {@link PrintOptions settings} pertaining to the print job
	 * @return a {@link CompletableFuture promise} that will return the rendered {@link BufferedImage preview}
	 */
	CompletableFuture<BufferedImage> screenshotJob(Document html, PrintOptions options);

}
