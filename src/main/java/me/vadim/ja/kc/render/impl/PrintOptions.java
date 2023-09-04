package me.vadim.ja.kc.render.impl;

import me.vadim.ja.kc.render.ConvertOptions;
import me.vadim.ja.kc.render.Margins;
import me.vadim.ja.kc.render.PageSize;

/**
 * @author vadim
 */
public class PrintOptions implements ConvertOptions {

	private final PageSize size;
	private final Margins margins;
	private final double zoom;
	private final boolean landscape;
	private final boolean printHeadersAndFooters;

	public PrintOptions(PageSize size, Margins margins, double zoom, boolean landscape, boolean printHeadersAndFooters) {
		this.size                   = size;
		this.margins                = margins;
		this.landscape              = landscape;
		this.zoom                   = zoom;
		this.printHeadersAndFooters = printHeadersAndFooters;
	}

	public PageSize getSize() {
		return size;
	}

	public Margins getMargins() {
		return margins;
	}

	public double getZoom() {
		return zoom;
	}

	public boolean isLandscape() {
		return landscape;
	}

	public boolean printHeadersAndFooters() {
		return printHeadersAndFooters;
	}

}
