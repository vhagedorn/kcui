package me.vadim.ja;

import me.vadim.ja.kc.KanjiCardEnvironment;

/**
 * @author vadim
 */
public final class Main {

	public static void main(String[] args) throws Exception {
		ApplicationEnvironment env = new KanjiCardEnvironment();
		System.out.println("Preparing environment.");
		env.preInit();
		System.out.println("Entering application.");
		Application app = env.createApplication();
		app.mainWindow(); // show app
		Runtime.getRuntime().addShutdownHook(new Thread(env::cleanUp));
	}

}