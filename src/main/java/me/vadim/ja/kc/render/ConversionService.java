package me.vadim.ja.kc.render;

import me.vadim.ja.kc.render.impl.PDFConversionService;
import me.vadim.ja.kc.render.impl.PreviewConversionService;

import java.util.concurrent.CompletableFuture;

/**
 * Defines a service that can convert {@link I input files} to {@link O output files}.
 *
 * @author vadim
 * @see PDFConversionService
 * @see PreviewConversionService
 */
public interface ConversionService<I, O> {

	/**
	 * Convert an {@link I} source to an {@link O} output.
	 * <p><b>This is a <i>blocking</i> method.</b>
	 *
	 * @param input   the {@link I input}
	 * @param options {@link ConvertOptions settings} pertaining to the job
	 *
	 * @return the converted {@link O output}
	 */
	O convert(I input, ConvertOptions options);

	/**
	 * Convert an {@link I} source to an {@link O} output.
	 * <p><b>This is an <i>asynchronous</i> method.</b>
	 *
	 * @param input   the {@link I input}
	 * @param options {@link ConvertOptions settings} pertaining to the job
	 *
	 * @return a {@link CompletableFuture promise} that will return the converted {@link O output}
	 */
	CompletableFuture<O> submitJob(I input, ConvertOptions options);

	/**
	 * End this {@link ConversionService}.
	 */
	void close();

}
