package me.vadim.ja.kc;

import com.formdev.flatlaf.intellijthemes.FlatOneDarkIJTheme;
import me.vadim.ja.Application;
import me.vadim.ja.ApplicationEnvironment;
import me.vadim.ja.kc.render.InMemoryFileServer;
import me.vadim.ja.kc.util.TeeStream;
import me.vadim.ja.kc.util.Util;

import javax.swing.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * @author vadim
 */
public class KanjiCardEnvironment implements ApplicationEnvironment {

	public static final File logfile = new File("kcui.log");

	@Override
	public void preInit() {
		System.out.print("Please wait..");
		try {
			Thread t = new Thread(Util::disableIllegalAccessWarning_v2);
			t.join(5 * 1000);
			if(t.isAlive()) { // failure
				System.err.println("WARN: Unable to supress illegal access warning, collapsing sterr to stout.");
				System.err.close(); // collapse stderr to stdout
				System.setErr(System.out);
			}
			if(t.isAlive())
				t.interrupt();
			if(t.isAlive())
				t.stop();
		} catch (InterruptedException e) {
			return;
		}
		System.out.println('.');

		try { // hacky temp solution for logging
			PrintStream log = new PrintStream(new FileOutputStream(logfile, true), true, StandardCharsets.UTF_8);
			System.setOut(new TeeStream(System.out, log));
			System.setErr(new TeeStream(System.err, log));
			log.print("===========================================================================================");
			log.print(new SimpleDateFormat(" yyyy-MM-dd @ HH:mm:ss z ").format(Calendar.getInstance().getTime()));
			log.print("===========================================================================================");
			log.println();
		} catch (IOException e) {
			System.err.println("Could not tee output to logfile " + logfile + "!");
			e.printStackTrace();
		}

		System.out.print("Loading FlatLaf");
		System.out.print('.');
		System.setProperty("flatlaf.useWindowDecorations", "true");
		System.setProperty("flatlaf.menuBarEmbedded", "true");
		System.out.print('.');
		FlatOneDarkIJTheme.setup(); // load FlatLaF theme
		System.out.print('.');
		UIManager.put("MenuItemUI", "me.vadim.ja.swing.CustomFlatMenuItemUI"); // custom accelerator text
		UIManager.put("Button.showMnemonics", true);
		InMemoryFileServer.dump = false;
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
