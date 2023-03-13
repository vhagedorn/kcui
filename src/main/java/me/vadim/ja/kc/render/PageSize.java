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
	 * @return width of the page in inches
	 */
	float width();

	/**
	 * @return height of the page in inches
	 */
	float height();

}
