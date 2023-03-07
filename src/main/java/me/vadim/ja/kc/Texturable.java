package me.vadim.ja.kc;

import javax.swing.*;
import java.awt.*;

/**
 * Represents a resizable {@link java.awt.Image} wrapped in an {@link Icon}.
 * @author vadim
 */
public interface Texturable {

	/**
	 * @return original {@link Image} instance passed to this {@link Texturable}
	 */
	Image unedited();

	/**
	 * Invert colors in the image.
	 */
	Texturable invert();

	/**
	 * Resize the image.
	 * @param w new width
	 * @param h new height
	 */
	Texturable withSize(int w, int h);

	/**
	 * @return the modified {@link Image}
	 */
	Image asImage();

	/**
	 * @return an {@link Icon} wrapping the modified {@link Image}
	 */
	Icon asIcon();

}
