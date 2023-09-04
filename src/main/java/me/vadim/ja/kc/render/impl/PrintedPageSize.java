package me.vadim.ja.kc.render.impl;

import me.vadim.ja.kc.render.DocConverters;
import me.vadim.ja.kc.render.PageSize;
import org.jetbrains.annotations.Nullable;

/**
 * @author vadim
 */
public class PrintedPageSize implements PageSize {

	private final String name;
	private final String unit;
	private final float w, h;

	public PrintedPageSize(String name, String unit, float w, float h) {
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
		return new PrintedPageSize(name, unit, (float) DocConverters.unitConvert(w + this.unit, unit), (float) DocConverters.unitConvert(h + this.unit, unit));
	}

}
