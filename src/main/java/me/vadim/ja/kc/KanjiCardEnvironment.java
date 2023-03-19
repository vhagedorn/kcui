package me.vadim.ja.kc;

import com.formdev.flatlaf.intellijthemes.FlatOneDarkIJTheme;
import me.vadim.ja.Application;
import me.vadim.ja.ApplicationEnvironment;

import javax.swing.*;

/**
 * @author vadim
 */
public class KanjiCardEnvironment implements ApplicationEnvironment {

	@Override
	public void preInit() {
		System.out.print("Loading FlatLaf.");
		System.setProperty("flatlaf.useWindowDecorations", "true");
		System.setProperty("flatlaf.menuBarEmbedded", "true");
		System.out.print('.');
		FlatOneDarkIJTheme.setup(); // load FlatLaF theme
		System.out.print('.');
		UIManager.put("MenuItemUI", "me.vadim.ja.swing.CustomFlatMenuItemUI"); // custom accelerator text
		UIManager.put("Button.showMnemonics", true);
		System.out.println(" done.");
	}

	@Override
	public Application createApplication() throws Exception {
		return new KanjiCardUI();
	}

	@Override
	public void cleanUp() {
		System.out.println("Goodbye.");
	}
}
