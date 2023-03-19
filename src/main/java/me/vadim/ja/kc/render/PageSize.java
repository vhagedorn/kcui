package me.vadim.ja.kc.render;

import org.jetbrains.annotations.Nullable;

/**
 * @author vadim
 */
public interface PageSize {

	/**
	 * May not be present for custom page sizes.
	 * @return the name for this page size
	 */
	@Nullable
	String name();

	/**
	 * @return unit that measures the {@link #width() width} and {@link #height() height}
	 */
	String unit();

	/**
	 * @return width of the page in {@link #unit() units}
	 */
	float width();

	/**
	 * @return height of the page in {@link #unit() units}
	 */
	float height();

	/**
	 * Convert this {@link PageSize} to another unit.
	 */
	PageSize withUnit(String unit);

}
