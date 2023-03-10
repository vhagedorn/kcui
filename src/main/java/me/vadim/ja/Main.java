package me.vadim.ja;

import com.formdev.flatlaf.intellijthemes.FlatOneDarkIJTheme;
import me.vadim.ja.kc.KanjiCardUI;

/**
 * @author vadim
 */
public final class Main {

	public static void main(String[] args) throws Exception {
		System.out.println("Loading FlatLaf...");
		System.setProperty("flatlaf.useWindowDecorations", "true");
		System.setProperty("flatlaf.menuBarEmbedded", "true");
		FlatOneDarkIJTheme.setup();
		System.out.println("Entering application.");
		Application app = new KanjiCardUI();
		app.mainWindow();
	}

}
