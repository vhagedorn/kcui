package me.vadim.ja.kc;

import org.imgscalr.Scalr;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.LookupOp;
import java.awt.image.LookupTable;
import java.io.IOException;
import java.net.URL;

/**
 * @author vadim
 */
public enum KCIcon {

	LOGO("icon.png", false),
	CLOSE("close.png"),
	MINIMIZE("minimize.png"),
	MAXIMIZE("maximize.png", "maximize2.png"),
	ADD("add.png"),
	REMOVE("remove.png"),
	DEBUG("debug.png", "debug2.png"),
	EDIT("edit.png", "edit2.png"),
	LIST("list.png", "list2.png"),
	SETTINGS("settings.png", "settings2.png"),
	BLOCK("block.png", false),
	;

	private final BufferedImage primary, secondary;

	KCIcon(String primary) {
		this(primary, KCTheme.DEFAULT_ICON_INVERT_STATE);
	}

	KCIcon(String primary, boolean invert) {
		this(primary, null, invert);
	}

	KCIcon(String primary, String secondary){
		this(primary, secondary, KCTheme.DEFAULT_ICON_INVERT_STATE);
	}

	KCIcon(String primary, String secondary, boolean invert){
		this.primary = loadImageResource(primary, invert);
		checkResource(this.primary, primary);

		this.secondary = loadImageResource(secondary, invert);
		if(secondary != null)
			checkResource(this.secondary, secondary);
	}

	@NotNull
	public Texturable getPrimary() {
		return new TexturableImpl(primary);
	}

	@Nullable
	public Texturable getSecondary(){
		return secondary == null ? null : new TexturableImpl(secondary);
	}

	private static void checkResource(Object ref, String name){
		if(ref == null)
			throw new IllegalStateException("Could not find resource with name '"+name+"'");
	}

	private static BufferedImage convertToARGB(BufferedImage image)
	{
		BufferedImage newImage = new BufferedImage(
				image.getWidth(), image.getHeight(),
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = newImage.createGraphics();
		g.drawImage(image, 0, 0, null);
		g.dispose();
		return newImage;
	}

	private static BufferedImage invert(BufferedImage image){
		if (image.getType() != BufferedImage.TYPE_INT_ARGB)
		{
			image = convertToARGB(image);
		}
		LookupTable lookup = new LookupTable(0, 4)
		{
			@Override
			public int[] lookupPixel(int[] src, int[] dest)
			{
				dest[0] = (int)(255-src[0]);
				dest[1] = (int)(255-src[1]);
				dest[2] = (int)(255-src[2]);
				return dest;
			}
		};
		LookupOp op = new LookupOp(lookup, new RenderingHints(null));
		return op.filter(image, null);
	}

	private static BufferedImage loadImageResource(String name, boolean invert) {
		if(name == null) return null;
		URL resource = KCIcon.class.getClassLoader().getResource(name);
		BufferedImage image;
		try {
			image = resource == null ? null : ImageIO.read(resource);
		} catch (IOException ignored){
			return null;
		}
		if(image == null)
			return null;
		return invert ? invert(image) : image;
	}

	private static class TexturableImpl implements Texturable {
		private final BufferedImage original;
		private BufferedImage cached;

		TexturableImpl(BufferedImage original) {
			this.original = cached = original;
		}

		@Override
		public Image unedited() {
			return original;
		}

		@Override
		public Texturable invert() {
			cached = KCIcon.invert(cached);
			return this;
		}

		@Override
		public Texturable withSize(int w, int h) {
			cached = Scalr.resize(original, Scalr.Method.BALANCED, Scalr.Mode.FIT_EXACT, w, h);
			return this;
		}

		@Override
		public Image asImage() {
			return cached;
		}

		@Override
		public Icon asIcon() {
			return new ImageIcon(cached);
		}
	}

}