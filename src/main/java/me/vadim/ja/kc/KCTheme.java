package me.vadim.ja.kc;

import javax.swing.*;
import java.awt.*;
import java.io.InputStream;

/**
 * @author vadim
 */
public final class KCTheme {

	public static Icon getButtonIcon(Texturable icon){
		return icon.withSize(20, 20).asIcon();
	}

	public static final boolean DEFAULT_ICON_INVERT_STATE = true;

	public static final Font JP_FONT;
	static {
		Font custom;
		try {
			InputStream resource = KCTheme.class.getClassLoader().getResourceAsStream("font/SawarabiMincho Regular 400.ttf");
			custom = Font.createFont(Font.TRUETYPE_FONT, resource);
		} catch (Exception e){
			System.err.println("WARN: Unable to load custom font. Defaulting to system font.");
			custom = new JLabel().getFont();
		}
		JP_FONT = new Font(custom.getName(), custom.getStyle(), 14);
	}

	public static final Color BACKGROUND = new Color(0x0F111A);

//	public static final Cursor CURSOR_BLOCKED;
//	static {
//		Toolkit toolkit = Toolkit.getDefaultToolkit();
//		Image image = KCIcon.BLOCK.getPrimary().asImage();
//		CURSOR_BLOCKED = toolkit.createCustomCursor(image , new Point(0, 0), "block");
//	}

}
