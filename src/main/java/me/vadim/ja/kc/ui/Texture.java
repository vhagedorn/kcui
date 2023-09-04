package me.vadim.ja.kc.ui;

import javax.swing.*;
import java.awt.image.BufferedImage;

/**
 * Represents a resizable {@link java.awt.Image} wrapped in an {@link Icon}.
 *
 * @author vadim
 */
public interface Texture {

	/**
	 * @return original {@link BufferedImage} instance passed to this {@link Texture}
	 */
	BufferedImage unedited();

	/**
	 * Invert colors in the image.
	 */
	Texture invert();

	/**
	 * Resize the image, ignoring aspect ratio.
	 *
	 * @param w new width
	 * @param h new height
	 */
	Texture withSize(int w, int h);

	/**
	 * Resize the image, preserving aspect ratio.
	 *
	 * @param w new width
	 */
	Texture withWidth(int w);

	/**
	 * Resize the image, preserving aspect ratio.
	 *
	 * @param h new height
	 */
	Texture withHeight(int h);

	/**
	 * @return the modified {@link BufferedImage}
	 */
	BufferedImage asImage();

	/**
	 * @return an {@link Icon} wrapping the modified {@link BufferedImage}
	 */
	Icon asIcon();

}
