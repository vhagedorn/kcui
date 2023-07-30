package me.vadim.ja.kc;

import me.vadim.ja.kc.impl.Icons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
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
	BACK("back.png"),
	LEFT("left.png"),
	RIGHT("right.png"),
	PREVIEW_EMPTY("preview_empty.png", false);

	private final BufferedImage primary, secondary;

	KCIcon(String primary) {
		this(primary, KCTheme.DEFAULT_ICON_INVERT_STATE);
	}

	KCIcon(String primary, boolean invert) {
		this(primary, null, invert);
	}

	KCIcon(String primary, String secondary) {
		this(primary, secondary, KCTheme.DEFAULT_ICON_INVERT_STATE);
	}

	KCIcon(String primary, String secondary, boolean invert) {
		this.primary = loadImageResource(primary, invert);
		checkResource(this.primary, primary);

		this.secondary = loadImageResource(secondary, invert);
		if (secondary != null)
			checkResource(this.secondary, secondary);
	}

	@NotNull
	public Texture getPrimary() {
		return Icons.wrap(primary);
	}

	@Nullable
	public Texture getSecondary() {
		return secondary == null ? null : Icons.wrap(secondary);
	}

	private static void checkResource(Object ref, String name) {
		if (ref == null)
			throw new IllegalStateException("Could not find resource with name '" + name + "'");
	}

	private static BufferedImage loadImageResource(String name, boolean invert) {
		if (name == null) return null;
		name = "img/" + name; // subfolder
		URL           resource = KCIcon.class.getClassLoader().getResource(name);
		BufferedImage image;
		try {
			image = resource == null ? null : ImageIO.read(resource);
		} catch (IOException ignored) {
			return null;
		}
		if (image == null)
			return null;
		return invert ? Icons.invert(image) : image;
	}

}