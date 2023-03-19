package me.vadim.ja.kc.render.impl;

import me.vadim.ja.kc.render.ConvertOptions;
import me.vadim.ja.kc.render.DocConverters;
import me.vadim.ja.kc.render.Margins;
import me.vadim.ja.kc.render.PageSize;
import org.jetbrains.annotations.Nullable;

/**
 * @author vadim
 */
public class PrintOptions implements ConvertOptions {

	private static class PSizeImpl implements PageSize {

		private final String name;
		private final String unit;
		private final float w, h;

		PSizeImpl(String name, float w, float h) {
			this(name, "in", w, h);
		}

		private PSizeImpl(String name, String unit, float w, float h) {
			this.name = name;
			this.unit = unit;
			this.w    = w;
			this.h    = h;
		}

		@Nullable
		@Override
		public String name() {
			return name;
		}

		@Override
		public String unit() {
			return unit;
		}

		@Override
		public float width() {
			return w;
		}

		@Override
		public float height() {
			return h;
		}

		@Override
		public PageSize withUnit(String unit) {
			return new PSizeImpl(name, unit, (float) DocConverters.unitConvert(w + this.unit, unit), (float) DocConverters.unitConvert(h + this.unit, unit));
		}
	}

	public static PrintOptions letter(){
		return new PrintOptions(new PSizeImpl("Letter", 8.5f, 11f),
								new Margins("in", 1, 1, 1, 1),
								false, true);
	}

	public static PrintOptions index(){
		return new PrintOptions(new PSizeImpl("Index Card", 4f, 6f),
								new Margins("in", .1f, .1f, .1f, .1f),
								false, false);
	}

	private final PageSize size;
	private final Margins margins;
	private final boolean  landscape;
	private final boolean  printHeadersAndFooters;

	public PrintOptions(PageSize size, Margins margins, boolean landscape, boolean printHeadersAndFooters) {
		this.size      = size;
		this.margins   = margins;
		this.landscape = landscape;
		this.printHeadersAndFooters = printHeadersAndFooters;
	}

	public PageSize getSize() {
		return size;
	}

	public Margins getMargins() {
		return margins;
	}

	public boolean isLandscape() {
		return landscape;
	}

	public boolean printHeadersAndFooters() {
		return printHeadersAndFooters;
	}

}
