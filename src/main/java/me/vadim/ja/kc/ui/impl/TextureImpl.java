package me.vadim.ja.kc.ui.impl;

import me.vadim.ja.kc.ui.Texture;
import org.imgscalr.Scalr;

import javax.swing.*;
import java.awt.image.BufferedImage;

/**
 * @author vadim
 */
class TextureImpl implements Texture {

	final BufferedImage original;
	BufferedImage cached;

	TextureImpl(BufferedImage original) {
		this.original = cached = original;
	}

	@Override
	public BufferedImage unedited() {
		return original;
	}

	@Override
	public Texture invert() {
		cached = Icons.invert(cached);
		return this;
	}

	@Override
	public Texture withSize(int w, int h) {
		cached = Scalr.resize(original, Scalr.Method.BALANCED, Scalr.Mode.FIT_EXACT, w, h);
		return this;
	}

	@Override
	public Texture withWidth(int w) {
		cached = Scalr.resize(original, Scalr.Method.BALANCED, Scalr.Mode.FIT_TO_WIDTH, w);
		return this;
	}

	@Override
	public Texture withHeight(int h) {
		cached = Scalr.resize(original, Scalr.Method.BALANCED, Scalr.Mode.FIT_TO_HEIGHT, h);
		return this;
	}

	@Override
	public BufferedImage asImage() {
		return cached;
	}

	@Override
	public Icon asIcon() {
		return new ImageIcon(cached);
	}

}
