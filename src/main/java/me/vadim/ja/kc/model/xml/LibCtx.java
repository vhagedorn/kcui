package me.vadim.ja.kc.model.xml;

import com.google.common.hash.HashCode;
import me.vadim.ja.kc.model.LibraryContext;
import me.vadim.ja.kc.model.Preferences;
import me.vadim.ja.kc.model.wrapper.Card;
import me.vadim.ja.kc.model.wrapper.Curriculum;
import me.vadim.ja.kc.model.wrapper.Group;
import me.vadim.ja.kc.model.wrapper.Library;
import me.vadim.ja.kc.render.DocConverters;
import me.vadim.ja.kc.render.impl.ctx.RenderContext;

import java.io.File;

/**
 * @author vadim
 */
public class LibCtx implements LibraryContext {

	public static final File libfile = new File(JAXBStorage.prefDir, "lib.xml");
	public static final File usrfile = new File(JAXBStorage.prefDir, "usr.xml");

	private final Library library;
	private final Preferences preferences;
	private final RenderContext context;

	public LibCtx() {
		Library lib = JAXBStorage.readLib(libfile);
		if (lib == null) {
			lib = new ImplLibrary();
			System.out.println("> Created new library.");
		}
		this.library = lib;
		System.out.println("> Using " + lib);
		lib.prune();

		Preferences pref = JAXBStorage.readPref(usrfile);
		if (pref == null)
			pref = new Preferences();
		this.preferences = pref;

		preferences.applyProperties();

		context = new RenderContext(
				this,
				DocConverters.print_jvppetteer(),
				DocConverters.preview_jvppetteer()
		);
	}

	@Override
	public Library getActiveLibrary() {
		return library;
	}

	@Override
	public Preferences getPreferences() {
		return preferences;
	}

	@Override
	public RenderContext getRenderContext() {
		return context;
	}
	@Override
	public void save(Card kanji) {
		System.out.println("Saving " + kanji.toPreviewString() + " to " + JAXBStorage.card2file(kanji));
		JAXBStorage.dumpCard(kanji);
		JAXBStorage.dumpLib(library, libfile);
	}

	@Override
	public void savePreferences() {
		JAXBStorage.dumpPref(preferences, usrfile);
	}

	@Override
	public void saveLibrary(boolean quiet) {
		if (!quiet)
			System.out.println("Saving " + library + " to " + libfile);
		JAXBStorage.dumpLib(library, libfile);
	}

	@Override
	public void delete(HashCode code, Location location) {
		JAXBStorage.card2file(location, code).delete();
	}

	@Override
	public void delete(Card kanji) {
		JAXBStorage.card2file(kanji).delete();
		library.unlinkCard(kanji);
	}

	@Override
	public void delete(Curriculum curriculum) {
		library.unlinkCurriculum(curriculum);
	}

	@Override
	public void delete(Group group) {
		group.getCurriculum().unlinkGroup(group);
	}

	@Override
	public void shutdown() {
		context.shutdown();
	}

}
