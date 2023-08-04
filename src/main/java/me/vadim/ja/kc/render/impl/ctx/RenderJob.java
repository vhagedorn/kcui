package me.vadim.ja.kc.render.impl.ctx;

import me.vadim.ja.kc.model.wrapper.Card;
import me.vadim.ja.kc.render.impl.svg.StrokePlotter;
import me.vadim.ja.kc.render.impl.svg.opt.DiagramOptions;
import me.vadim.ja.kc.render.impl.svg.opt.Rendering;
import me.vadim.ja.kc.util.Util;
import org.apache.batik.transcoder.TranscoderException;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author vadim
 */
public class RenderJob extends AbstractJob<String[]> {

	private final Card card;

	public RenderJob(RenderContext context, Card card) {
		super(context);
		this.card = card;
	}

	private DiagramOptions diag;

	public RenderJob withOpts(DiagramOptions diag) {
		this.diag = diag;
		return this;
	}

	public RenderJob withOpts(int diag) {
		this.diag = DiagramOptions.fromBitmask(diag);
		return this;
	}

	@Override
	public CompletableFuture<String[]> getResult() {
		return result == null ? (result =
				CompletableFuture.supplyAsync(() -> {
					// filter kanji
					int[] codepoints = card.describeJapanese().codePoints().filter(Character::isIdeographic).toArray();

					String[] imgs = new String[codepoints.length];
					for (int i = 0; i < imgs.length; i++)
						 imgs[i] = context.diagrams.computeIfAbsent(diag.toBitmask(), x -> new ConcurrentHashMap<>())
												   .computeIfAbsent(codepoints[i], ch ->
														   CompletableFuture.supplyAsync(() -> {
															   String d;

															   StrokePlotter plotter = context.plotters.computeIfAbsent(ch, StrokePlotter::kvg);
															   try {
																   BufferedImage image = plotter.renderCombined(Rendering.default_sopt, diag, Rendering.default_opts);
																   d = Util.imgToBase64String(image, "png");
															   } catch (IOException e) {
																   throw new UncheckedIOException(e);
															   } catch (TranscoderException e) {
																   throw new RuntimeException(e);
															   }

															   return d;
														   }, context.worker))
												   .exceptionally(x -> {
													   x.printStackTrace();
													   return null;
												   }).join(); // only create 1 task per character

					if (diag.isRTL())
						//reverse images for RTL consistency
						Util.reverse(imgs);

					return imgs;
				}, context.worker)
		) : result;
	}

}
