package me.vadim.ja.kc.render.impl.ctx;

import me.vadim.ja.kc.model.wrapper.Card;
import me.vadim.ja.kc.render.impl.PrintOptions;
import me.vadim.ja.kc.render.impl.Printing;
import me.vadim.ja.kc.render.impl.factory.Generator;
import me.vadim.ja.kc.render.impl.factory.PDFUtil;
import me.vadim.ja.kc.util.Util;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author vadim
 */
public class ExportJob extends AbstractJob<PDDocument[]> {

	private final List<Card> cards;

	ExportJob(RenderContext context, List<Card> cards) {
		super(context);
		this.cards = new ArrayList<>(cards);
	}

	public ExportJob orderByGroups() {
		Comparator<Card> cmp = Comparator
				.<Card, String>comparing(card -> String.valueOf(card.getLocation().getCurriculum()))
				.thenComparing(card -> String.valueOf(card.getLocation().getGroup()));

		cards.sort(cmp);
		return this;
	}

	private boolean split = true;

	public ExportJob splitFrontAndBack(boolean create2SeparateFiles) {
		this.split = create2SeparateFiles;
		return this;
	}

	private Runnable update;

	public ExportJob sendProgressUpdates(Runnable toUpdate) {
		this.update = toUpdate;
		return this;
	}

	public int getExpectedUpdatePollCount() {
		return (cards.size() * 4) + 1;
	}

	private <T> T poll(T t) {
		if (update != null)
			update.run();
		return t;
	}

	private <T> CompletableFuture<T> poll(CompletableFuture<T> f) {
		return f.thenApply(this::poll);
	}

	@Override
	@SuppressWarnings("CodeBlock2Expr")
	public CompletableFuture<PDDocument[]> getResult() {
		return result == null ? (result =
				Util.combineFutures(cards.stream()
										 .map(k -> {
											 return context.createRender(k)
														   .withOpts(k.getRenderOpts())
														   .getResult()
														   .thenApply(this::poll)
														   .thenApply(imgs -> {
															   Generator gen = new Generator(k);
															   return new Document[] {
																	   gen.createFront(),
																	   gen.createBack(imgs)
															   };
														   });
										 })
										 .map(this::poll)
										 .map(f -> {
											 return f.thenApply(docs -> {
												 PrintOptions opts = Printing.ofMinimal(Printing.INDEX_CARD);
												 return new PDDocument[] {
														 PDFUtil.takePages(context.pdf.createPDF(docs[0], opts), true, 0),
														 PDFUtil.takePages(context.pdf.createPDF(docs[1], opts), true, 0)
												 };
											 });
										 })
										 .map(this::poll)
										 .map(f -> f.thenApply(PDFUtil::mergePDFsClosing))
										 .map(this::poll))
					.thenApply(pdfs -> {
						PDDocument[] docus = pdfs.toArray(PDDocument[]::new);
						if (split)
							return new PDDocument[] {
									PDFUtil.mergePDFs(docus, false, 0),
									PDFUtil.mergePDFs(docus, true, 1)
							};
						else
							return new PDDocument[] {
									PDFUtil.mergePDFs(docus, true)
							};
					})
					.thenApply(this::poll)
					.exceptionally(x -> {
						System.err.println("Problem when exporting:");
						x.printStackTrace();
						return null;
					})
		) : result;
	}

}
