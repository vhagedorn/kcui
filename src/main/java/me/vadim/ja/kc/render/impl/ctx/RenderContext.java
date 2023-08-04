package me.vadim.ja.kc.render.impl.ctx;

import me.vadim.ja.kc.KanjiCardUI;
import me.vadim.ja.kc.model.LibraryContext;
import me.vadim.ja.kc.model.wrapper.Card;
import me.vadim.ja.kc.model.wrapper.Curriculum;
import me.vadim.ja.kc.model.wrapper.Group;
import me.vadim.ja.kc.render.impl.PDFConversionService;
import me.vadim.ja.kc.render.impl.PreviewConversionService;
import me.vadim.ja.kc.render.impl.svg.StrokePlotter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

/**
 * @author vadim
 */
public class RenderContext {

	protected final LibraryContext lib;

	protected final PDFConversionService pdf;
	protected final PreviewConversionService png;

	protected final ExecutorService worker = KanjiCardUI.threadPool("Render Worker %d");

	public RenderContext(LibraryContext lib, PDFConversionService pdf, PreviewConversionService png) {
		this.lib = lib;

		this.pdf = pdf;
		this.png = png;
	}

	/* Diagrams */

	// <codepoint, plotter>
	protected final Map<Integer, StrokePlotter> plotters = new ConcurrentHashMap<>();

	// <diag, <codepoint, future<base64>>>
	protected final Map<Integer, Map<Integer, CompletableFuture<String>>> diagrams = new ConcurrentHashMap<>();

	public RenderJob createRender(Card card) {
		return new RenderJob(this, card);
	}

	/* Previews */

	// <kanji, job>
	protected final Map<String, PreviewJob> previews = new ConcurrentHashMap<>();

	public PreviewJob createPreview(Card card) {
		PreviewJob job = new PreviewJob(this, card);
		previews.put(card.describeJapanese(), job);
		return job;
	}

	public PreviewJob cachedPreview(Card card) {
		return previews.computeIfAbsent(card.describeJapanese(), (x) -> new PreviewJob(this, card));
	}

	/* Exports */

	public ExportJob createExport(Curriculum curriculum) {
		return new ExportJob(this, lib.getActiveLibrary().getCards(curriculum));
	}

	public ExportJob createExport(List<Group> groups) {
		return new ExportJob(this, groups.stream().flatMap(g -> lib.getActiveLibrary().getCards(g).stream()).collect(Collectors.toList()));
	}

	public ExportJob createExport(Card... cards) {
		return new ExportJob(this, Arrays.asList(cards));
	}

	public void shutdown() {
		png.close();
		pdf.close();
	}

}
