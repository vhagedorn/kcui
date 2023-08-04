package me.vadim.ja.kc.render.impl.svg.opt;

import java.awt.*;

/**
 * @author vadim
 */
public class StrokesOptions {

	public final Color drawn, onion, point;
	public final int point_r;
	public final boolean onion_future;

	/**
	 * @param drawn color of active/past lines
	 * @param onion color of future/past lines
	 * @param point color of starting points
	 * @param point_r radius of starting points
	 * @param onion_future {@code true} to color future lines in onion, {@code false} to color past lines in onion
	 */
	public StrokesOptions(Color drawn, Color onion, Color point, int point_r, boolean onion_future) {
		this.drawn        = drawn;
		this.onion        = onion;
		this.point        = point;
		this.point_r      = point_r;
		this.onion_future = onion_future;
	}

}
