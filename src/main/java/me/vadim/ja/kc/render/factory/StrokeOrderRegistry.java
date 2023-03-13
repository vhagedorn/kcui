package me.vadim.ja.kc.render.factory;

import me.vadim.ja.kc.db.impl.img.ImageCache;
import me.vadim.ja.kc.wrapper.Kanji;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author vadim
 */
public class StrokeOrderRegistry {

	private final DiagramCreator diag;
	private final int opts;
	private ImageCache db;

	public StrokeOrderRegistry(DiagramCreator diag) {
		this.diag = diag;
		this.opts = diag.toBitmask();
	}

	public String[] queryDiagrams(Kanji target){
		List<String> imgs = target.value.codePoints().mapToObj(c -> {
			String character = Character.toString(c);

			String base64 = db.queryDiagram(character, opts);

			if(base64 == null) {
				base64 = diag.strokeOrder(character);
				db.insertDiagram(character, opts, base64);
			}

			return base64;
		}).collect(Collectors.toList());

		if (diag.isRTL()) {
			//reverse images for RTL consistency
			Collections.reverse(imgs);
		}

		return imgs.toArray(String[]::new);
	}

}
