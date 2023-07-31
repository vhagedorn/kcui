package me.vadim.ja.kc.ui.impl;

import me.vadim.ja.kc.ui.Texture;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.LookupOp;
import java.awt.image.LookupTable;

/**
 * @author vadim
 */
public final class Icons {

	public static Texture wrap(BufferedImage image) {
		return new TextureImpl(image);
	}

	public static BufferedImage convertToARGB(BufferedImage image) {
		BufferedImage newImage = new BufferedImage(
				image.getWidth(), image.getHeight(),
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = newImage.createGraphics();
		g.drawImage(image, 0, 0, null);
		g.dispose();
		return newImage;
	}

	public static BufferedImage invert(BufferedImage image) {
		if (image.getType() != BufferedImage.TYPE_INT_ARGB) {
			image = convertToARGB(image);
		}
		LookupTable lookup = new LookupTable(0, 4) {
			@Override
			public int[] lookupPixel(int[] src, int[] dest) {
				dest[0] = (int) (255 - src[0]);
				dest[1] = (int) (255 - src[1]);
				dest[2] = (int) (255 - src[2]);
				return dest;
			}
		};
		LookupOp op = new LookupOp(lookup, new RenderingHints(null));
		return op.filter(image, null);
	}


}
