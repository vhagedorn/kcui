package me.vadim.ja.kc.render.impl.svg.opt;

import me.vadim.ja.kc.render.impl.svg.StrokePlotter;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Default values for option classes within this package.
 *
 * @author vadim
 */
public class Rendering {

	public static final Color tanoshii_brown = new Color(96, 57, 19);
	public static final Color tanoshii_yellow = new Color(234, 174, 106);
	public static final Color tanoshii_onion = new Color(181, 181, 181);
	public static final Color matplotlib_brown = new Color(165, 44, 44);

	public static final StrokesOptions default_sopt = new StrokesOptions(Color.black, tanoshii_onion, Color.red, 3, false);
	public static final DiagramOptions default_diag = new DiagramOptions(200, true, 5, DiagramOptions.DOWN);
	public static final CollageOptions default_opts = new CollageOptions(.01f, .013f, matplotlib_brown, 3f, null, tanoshii_yellow, 1f / 2f, new float[] { 1f });

	// the grid_epsilons aren't perfect when tiling
	// they look fine as s:.01f and e:.02f, but when
	// you put 2 of them next to each other weird shit
	// begins to happen, so e:.013f is close enough

	public static BufferedImage render_default(StrokePlotter plotter) throws Exception {
		return plotter.renderCombined(default_sopt, default_diag, default_opts);
	}

}
