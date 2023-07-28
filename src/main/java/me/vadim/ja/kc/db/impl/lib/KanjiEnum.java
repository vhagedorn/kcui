package me.vadim.ja.kc.db.impl.lib;

import me.vadim.ja.kc.db.DbEnum;
import me.vadim.ja.kc.db.DbMultimap;
import me.vadim.ja.kc.db.impl.DbEnumAdapter;
import me.vadim.ja.kc.wrapper.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author vadim
 */
class KanjiEnum extends DbEnumAdapter<Kanji> {

	private /*static*/ Kanji create(String value, long c_id, long g_id, long id) {
		Curriculum curriculum = curriculums.select(c_id);
		if(curriculum == null)
			throw new IllegalStateException(String.format("Invalid c_id (%d) for kanji (%d).", c_id, id));

		Group group = null;
		for (Group g : curriculum.groups)
			if(g.id() == g_id) {
				group = g;
				break;
			}
		if(group == null)
			throw new IllegalStateException(String.format("Invalid g_id (%d) for kanji (%d).", g_id, id));

		Kanji kanji = new Kanji(value, group);
		kanji.setId(id);

		lock.lock();
		try {
			kanji.partsOfSpeech.addAll(partsOfSpeech.get(kanji));
			kanji.pronounciations.addAll(pronounciations.get(kanji));
			kanji.definitions.addAll(definitions.get(kanji));
		} finally {
			lock.unlock();
		}

		return kanji;
	}


	private final DbEnum<Curriculum>                curriculums;
	private final DbMultimap<Kanji, PartOfSpeech>   partsOfSpeech;
	private final DbMultimap<Kanji, Pronounciation> pronounciations;
	private final DbMultimap<Kanji, Definition>     definitions;

	KanjiEnum(ReentrantLock lock, DbEnum<Curriculum> curr, DbEnum<PartOfSpeech> pos) {
		super(lock, Kanji[]::new);
		this.curriculums     = curr;
		this.partsOfSpeech   = new PoSMultimap(lock, pos);
		this.pronounciations = new PronMultimap(lock);
		this.definitions     = new DefMultimap(lock);
	}

	@Override
	protected void createTables() throws SQLException {
		connection.prepareStatement("create table if not exists CARDS(id INTEGER PRIMARY KEY, kanji TEXT, c_id INTEGER, g_id INTEGER)").execute();
		partsOfSpeech.initialize(connection);
		pronounciations.initialize(connection);
		definitions.initialize(connection);
	}

	private void clear(Kanji kanji) {
		lock.lock();
		try {
			partsOfSpeech.clear(kanji);
			pronounciations.clear(kanji);
			definitions.clear(kanji);
		} finally {
			lock.unlock();
		}
	}

	@Override
	protected void implDelete(long id) throws SQLException {
		Kanji q = select(id);

		if (q != null)
			clear(q);

		PreparedStatement statement = connection.prepareStatement("delete from CARDS where id=?");
		statement.setLong(1, id);
		runLocking(statement::execute);
	}

	@Override
	protected void implInsert(Kanji obj) throws SQLException {
		PreparedStatement statement = connection.prepareStatement("insert into CARDS (kanji, c_id, g_id) VALUES (?, ?, ?)");
		statement.setString(1, obj.value);
		statement.setLong(2, obj.curriculum.id());
		statement.setLong(3, obj.group.id());
		runLocking(statement::execute);

		ResultSet result = statement.getGeneratedKeys();
		if (result.next()) {
			if (!obj.hasId()) // hehe thread safety go brr
				obj.setId(result.getLong(1));
		}

		clear(obj);
		partsOfSpeech.put(obj, obj.partsOfSpeech);
		pronounciations.put(obj, obj.pronounciations);
		definitions.put(obj, obj.definitions);
	}

	@Override
	protected void implUpdate(Kanji obj) throws SQLException {
		if (!obj.hasId())
			throw new IllegalArgumentException("id not set");

		PreparedStatement statement = connection.prepareStatement("update CARDS set kanji=?, c_id=?, g_id=? where id=?");
		statement.setString(1, obj.value);
		statement.setLong(2, obj.curriculum.id());
		statement.setLong(3, obj.group.id());
		statement.setLong(4, obj.id());
		runLocking(statement::execute);

		clear(obj);
		partsOfSpeech.put(obj, obj.partsOfSpeech);
		pronounciations.put(obj, obj.pronounciations);
		definitions.put(obj, obj.definitions);
	}


	@Override
	protected boolean hasId(Kanji obj) {
		return obj.hasId();
	}

	@Override
	protected void setId(Kanji obj, long id) {
		obj.setId(id);
	}

	@Override
	protected long[] implSelect(Kanji obj) throws SQLException {
		PreparedStatement statement = connection.prepareStatement("select id from CARDS where kanji=? AND c_id=? AND g_id=?");
		statement.setString(1, obj.value);
		statement.setLong(2, obj.curriculum.id());
		statement.setLong(3, obj.group.id());

		return parseKeySet(statement.executeQuery());
	}

	@Override
	protected Kanji implSelect(long id) throws SQLException {
		PreparedStatement statement = connection.prepareStatement("select kanji, c_id, g_id from CARDS where id=?");
		statement.setLong(1, id);
		ResultSet result = statement.executeQuery();

		if (!result.next())
			return null;

		return create(result.getString(1), result.getLong(2), result.getLong(3), id);
	}

	@Override
	protected PreparedStatement selectAllQuery(boolean count) throws SQLException {
		return connection.prepareStatement(count
										   ? selectCountStatement("CARDS")
										   : "select kanji, c_id, g_id, id from CARDS");
	}

	@Override
	protected Kanji selectAllBuild(ResultSet result) throws SQLException {
		return create(result.getString(1), result.getLong(2), result.getLong(3), result.getLong(4));
	}
}
