package me.vadim.ja.kc.render.impl.img;

import me.vadim.ja.kc.KanjiCardUI;
import me.vadim.ja.kc.db.impl.blob.BlobCache;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

/**
 * @author vadim
 */
public class StrokeOrderRegistry {

	public static final int DEFAULT_OPTS = 0;

	private final DiagramCreator diag;
	private final ExecutorService worker = KanjiCardUI.threadPool("Stroke diagram renderer %d");

	private final BlobCache db;

	public StrokeOrderRegistry(DiagramCreator diag, BlobCache db) {
		this.diag = diag;
		this.db   = db;
	}

	public String[] queryDiagrams(String target, int options) {
		DiagramCreator diag = options == DEFAULT_OPTS ? this.diag : this.diag.withOptions(options);
		int            opts = diag.toBitmask();

		db.connect();
		List<String> imgs = target.codePoints().filter(Character::isIdeographic).mapToObj(c -> {
			String character = Character.toString(c);

			String base64 = db.queryDiagram(character, opts);

			if (base64 == null) {
				base64 = diag.strokeOrder(character);
				db.insertDiagram(character, opts, base64);
			}

			return base64;
		}).collect(Collectors.toList());
		db.disconnect();

		if (diag.isRTL()) {
			//reverse images for RTL consistency
			Collections.reverse(imgs);
		}
		return imgs.toArray(String[]::new);
	}

	public CompletableFuture<String[]> submitQuery(String target, int options) {
		return CompletableFuture.supplyAsync(() -> queryDiagrams(target, options), worker);
	}

}
