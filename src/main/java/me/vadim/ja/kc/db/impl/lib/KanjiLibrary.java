package me.vadim.ja.kc.db.impl.lib;

import me.vadim.ja.kc.db.DbEnum;
import me.vadim.ja.kc.db.impl.Sqlite3Database;
import me.vadim.ja.kc.wrapper.*;

import java.sql.SQLException;

/**
 * @author vadim
 */
public final class KanjiLibrary extends Sqlite3Database {

	private final DbEnum<PartOfSpeech> pos;
	private final DbEnum<Curriculum>   curriculums;
	private final DbEnum<Kanji>        cards;

	public KanjiLibrary() {
		super("library.db");
		pos         = new PoSEnum();
		curriculums = new CurriculumEnum();
		cards = new KanjiEnum(curriculums, pos);
	}

	@Override
	protected void onConnect() throws SQLException {
		pos.initialize(connection);
		curriculums.initialize(connection);
		cards.initialize(connection);
	}

	public DbEnum<PartOfSpeech> getPartOfSpeech() {
		return pos;
	}

	public DbEnum<Curriculum> getCurriculums() {
		return curriculums;
	}

	public DbEnum<Kanji> getCards() {
		return cards;
	}

	public static void main(String[] args) {
		KanjiLibrary lib = new KanjiLibrary();
		lib.connect();

		CurriculumManager mgr = CurriculumManager.cringe;
		DbEnum<Kanji> k = lib.getCards();
		DbEnum<PartOfSpeech> p = lib.getPartOfSpeech();
		DbEnum<Curriculum> c = lib.getCurriculums();

//		for (PartOfSpeech value : p.values())
//			p.delete(value.id());
//
//		for (PartOfSpeech part : mgr.partsOfSpeech())
//			p.create(part);

		Kanji myK = new Kanji("The", c.values()[0].getGroups().get(0));
		myK.addDefinition("define me");
		myK.addPartOfSpeech(p.values()[0]);
		myK.addPronounciation(PronounciationType.UNKNOWN, "yeet");

		System.out.println(myK.toPreviewString());

		k.update(myK);

		for (Kanji kanji : k.values())
			System.out.println(kanji.toPreviewString());

		myK.definitions.clear();
		myK.addDefinition("Bruh");
		myK.pronounciations.clear();
		myK.addPronounciation(PronounciationType.KUN_YOMI, "lmfao");

		k.update(myK);

		for (Kanji kanji : k.values())
			System.out.println(kanji.toPreviewString());

		lib.disconnect();
	}

}
