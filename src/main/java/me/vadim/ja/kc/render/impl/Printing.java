package me.vadim.ja.kc.render.impl;

import me.vadim.ja.kc.render.Margins;
import me.vadim.ja.kc.render.PageSize;

/**
 * @author vadim
 */
public class Printing {

	/**
	 * ISO A4.
	 */
	public static final PageSize A4 = new PrintedPageSize("A4", "mm", 210f, 297f);

	/**
	 *  American version of A4, dubbed "Letter".
	 */
	public static final PageSize LETTER = new PrintedPageSize("Letter", "in", 8.5f, 11f);

	/**
	 * 4x6 Index Card.
	 */
	public static final PageSize INDEX_CARD = new PrintedPageSize("Index Card", "in", 4f, 6f);

	public static PrintOptions ofStandard(PageSize size) {
		return new PrintOptions(size,
								new Margins("in", 1, 1, 1, 1),
								1.0, false, true);
	}

	public static PrintOptions ofMinimal(PageSize size) {
		return new PrintOptions(size,
								new Margins("in", .1f, .1f, .1f, .1f),
								1.0, false, false);
	}

	public static PrintOptions ofZoomed(PageSize size, double zoom) {
		return new PrintOptions(size,
								new Margins("in", .1f, .1f, .1f, .1f),
								zoom, false, false);
	}

}
