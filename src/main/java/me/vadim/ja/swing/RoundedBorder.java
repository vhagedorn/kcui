package me.vadim.ja.swing;

import javax.swing.border.Border;
import java.awt.*;

/**
 * <a href="https://stackoverflow.com/a/3634480/12344841">source</a>
 * @author Lalchand
 */
public class RoundedBorder implements Border {

	private int radius;


	public RoundedBorder(int radius) {
		this.radius = radius;
	}


	public Insets getBorderInsets(Component c) {
		return new Insets(this.radius+1, this.radius+1, this.radius+2, this.radius);
	}


	public boolean isBorderOpaque() {
		return true;
	}


	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
		g.drawRoundRect(x, y, width-1, height-1, radius, radius);
	}
}