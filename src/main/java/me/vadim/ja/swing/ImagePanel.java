package me.vadim.ja.swing;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ImagePanel extends JPanel implements SwingConstants {

	private BufferedImage image;

	private int sticksTo = SwingConstants.CENTER;

	public ImagePanel() {}

	public ImagePanel(BufferedImage image) {
		this.image = image;
	}

	/**
	 * @see #setSticksTo(int) sticksTo details
	 */
	public ImagePanel(BufferedImage image, int sticksTo) {
		this.image = image;
		this.sticksTo = sticksTo;
	}

	/**
	 * Default value is {@link SwingConstants#CENTER}
	 * @see SwingConstants#NORTH
	 * @see SwingConstants#EAST
	 * @see SwingConstants#SOUTH
	 * @see SwingConstants#WEST
	 * @see SwingConstants#CENTER
	 * @see SwingConstants#NORTH_EAST
	 * @see SwingConstants#NORTH_WEST
	 * @see SwingConstants#SOUTH_EAST
	 * @see SwingConstants#SOUTH_WEST
	 */
	public void setSticksTo(int direction) {
		this.sticksTo = direction;
	}

	public int getSticksTo(){
		return this.sticksTo;
	}

	public void setImage(BufferedImage image) {
		this.image = image;
	}

	public BufferedImage getImage() {
		return this.image;
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(image.getWidth(), image.getHeight());
	}

	@Override
	public Dimension getMinimumSize() {
		return getPreferredSize();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Dimension size = getSize();
		Dimension sImg = getPreferredSize();

		// CENTER by default
		int x = (size.width / 2) - (sImg.width / 2);
		int y = (size.height / 2) - (sImg.height / 2);

		int maxX = size.width - sImg.width;
		int maxY = size.height - sImg.height;

		switch (sticksTo) {
			case NORTH_WEST:
				x = 0;
			case NORTH:
				y = 0;
				break;
			case NORTH_EAST:
				y = 0;
			case EAST:
				x = maxX;
				break;
			case SOUTH_WEST:
				x = 0;
			case SOUTH:
				y = maxY;
				break;
			case SOUTH_EAST:
				x = maxX;
				y = maxY;
				break;
			case WEST:
				x = 0;
				break;
		}

		// default to NORTH_WEST if image > size
		x = Math.max(x, 0);
		y = Math.max(y, 0);

		g.drawImage(image, x, y, this);
	}

}