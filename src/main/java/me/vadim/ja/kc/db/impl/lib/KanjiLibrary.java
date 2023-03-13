package me.vadim.ja.kc.db.impl.lib;

import me.vadim.ja.kc.db.DbEnum;
import me.vadim.ja.kc.db.Sqlite3Connector;
import me.vadim.ja.kc.wrapper.Curriculum;
import me.vadim.ja.kc.wrapper.Kanji;
import me.vadim.ja.kc.wrapper.PartOfSpeech;

import java.sql.SQLException;

/**
 * @author vadim
 */
public class KanjiLibrary extends Sqlite3Connector {

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
//		connection.prepareStatement("drop table CURRICULUMS; drop table GROUPS").execute();
//		connection.setAutoCommit(false);
//		connection.commit();
//		connection.setAutoCommit(true);
		System.out.println("DROPPED TABLES ");
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

		DbEnum<Kanji> k = lib.getCards();



		lib.disconnect();
	}

}
