package me.vadim.ja.kc.render.impl.svg.opt;

import java.awt.*;

/**
 * @author vadim
 */
public class CollageOptions {

	public final float grid_epsilon_s;
	public final float grid_epsilon_e;
	public final Color outer_color;
	public final float outer_thick;
	public final float[] outer_dash;
	public final Color inner_color;
	public final float inner_thick;
	public final float[] inner_dash;

	/**
	 * @param grid_epsilon_s offset at start of grid (to prevent cutting off the lines around the edge)
	 * @param grid_epsilon_e offset at end   of grid (to prevent cutting off the lines around the edge)
	 * @param outer_color color of the outer lines
	 * @param outer_thick thickness of the outer lines
	 * @param outer_dash dash pattern (or null) of the outer lines
	 * @param inner_color color of the inner lines
	 * @param inner_thick thickness of the inner lines
	 * @param inner_dash dash pattern (or null) of the inner lines
	 */
	public CollageOptions(float grid_epsilon_s, float grid_epsilon_e, Color outer_color, float outer_thick, float[] outer_dash, Color inner_color, float inner_thick, float[] inner_dash) {
		this.grid_epsilon_s = grid_epsilon_s;
		this.grid_epsilon_e = grid_epsilon_e;
		this.outer_color    = outer_color;
		this.outer_thick    = outer_thick;
		this.outer_dash     = outer_dash;
		this.inner_color    = inner_color;
		this.inner_thick    = inner_thick;
		this.inner_dash     = inner_dash;
	}

}
