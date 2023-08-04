package me.vadim.ja.kc.render.impl.ctx;

import me.vadim.ja.kc.model.wrapper.Card;
import me.vadim.ja.kc.render.impl.PrintOptions;
import me.vadim.ja.kc.render.impl.Printing;
import me.vadim.ja.kc.render.impl.factory.Generator;
import me.vadim.ja.kc.render.impl.svg.opt.DiagramOptions;
import me.vadim.ja.kc.render.impl.svg.opt.Rendering;
import org.jsoup.nodes.Document;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author vadim
 */
public class PreviewJob extends AbstractJob<BufferedImage[]> {

	private final Card card;

	public PreviewJob(RenderContext context, Card card) {
		super(context);
		this.card = card;
	}

	private DiagramOptions diag = Rendering.default_diag;

	public PreviewJob withOpts(DiagramOptions diag) {
		this.diag = diag;
		return this;
	}

	public PreviewJob withOpts(int diag) {
		this.diag = DiagramOptions.fromBitmask(diag);
		return this;
	}

	private float zoomFactor = 1.0f;

	public PreviewJob zoomTo(float zoomFactor) {
		this.zoomFactor = zoomFactor;
		return this;
	}

	@Override
	public CompletableFuture<BufferedImage[]> getResult() {
		return result == null ? (result =
				context.createRender(card).withOpts(diag).getResult().thenComposeAsync(imgs -> {
					Generator gen   = new Generator(card);
					Document  front = gen.createFront();
					Document  back  = gen.createBack(imgs);

					PrintOptions opts = Printing.ofZoomed(Printing.INDEX_CARD, zoomFactor);
					List<CompletableFuture<BufferedImage>> jobs =
							Arrays.asList(
									context.png.submitJob(front, opts),
									context.png.submitJob(back, opts));

					return CompletableFuture.allOf(jobs.toArray(CompletableFuture[]::new))
											.thenApplyAsync(x -> jobs.stream().map(CompletableFuture::join).toArray(BufferedImage[]::new), context.worker);
				}, context.worker)
		) : result;
	}

}
