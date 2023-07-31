package me.vadim.ja.kc.render.impl.img;

import me.vadim.ja.kc.KanjiCardUI;
import me.vadim.ja.kc.db.ImageCache;
import me.vadim.ja.kc.util.Util;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

/**
 * @author vadim
 */
public class StrokeOrderRegistry {

	public static final int DEFAULT_OPTS = 0;

	private final DiagramCreator diag;
	private final ExecutorService worker = KanjiCardUI.threadPool("Stroke diagram renderer %d");

	private final Map<String, CompletableFuture<String>> diagrams = new ConcurrentHashMap<>();

	private final ImageCache db;

	public StrokeOrderRegistry(DiagramCreator diag, ImageCache db) {
		this.diag = diag;
		this.db   = db;
	}

	public String[] queryDiagrams(String target, int options) {
		DiagramCreator diag = options == DEFAULT_OPTS ? this.diag : this.diag.withOptions(options);
		int            opts = diag.toBitmask();

		diag.asyncWorker = worker;
		// filter kanji
		target = target.codePoints().filter(Character::isIdeographic).mapToObj(Character::toString).collect(StringBuilder::new, StringBuilder::append, StringBuilder::append).toString();
		String[] imgs = db.queryMultiple(target, opts);
		for (int i = 0; i < imgs.length; i++) {
			String img = imgs[i];
			if (img == null)
				img = diagrams.computeIfAbsent(Character.toString(target.codePointAt(i)), ch ->
						CompletableFuture.supplyAsync(() -> {
							String d = diag.strokeOrder(ch);
							db.insertOne(ch, opts, d);
							return d;
						}, worker)).join(); // only create 1 task per character
			imgs[i] = img;
		}

		if (diag.isRTL())
			//reverse images for RTL consistency
			Util.reverse(imgs);
		return imgs;
	}

	public CompletableFuture<String[]> submitQuery(String target, int options) {
		return CompletableFuture.supplyAsync(() -> queryDiagrams(target, options), worker);
	}

}
