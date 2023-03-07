package me.vadim.ja;

import com.formdev.flatlaf.intellijthemes.FlatOneDarkIJTheme;
import me.vadim.ja.kc.KanjiCardUI;

/**
 * @author vadim
 */
public final class Main {

	public static void main(String[] args) throws Exception {
		System.setProperty("flatlaf.useWindowDecorations", "true");
		System.setProperty("flatlaf.menuBarEmbedded", "true");
		FlatOneDarkIJTheme.setup();
		Application app = new KanjiCardUI();
		app.mainWindow();
	}

}
