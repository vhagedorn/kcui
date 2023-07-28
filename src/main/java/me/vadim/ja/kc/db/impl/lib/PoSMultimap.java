package me.vadim.ja.kc.db.impl.lib;

import me.vadim.ja.kc.db.DbEnum;
import me.vadim.ja.kc.db.impl.DbMultimapAdapter;
import me.vadim.ja.kc.wrapper.Kanji;
import me.vadim.ja.kc.wrapper.PartOfSpeech;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author vadim
 */
class PoSMultimap extends DbMultimapAdapter<Kanji, PartOfSpeech> {

	private final DbEnum<PartOfSpeech> reg;

	PoSMultimap(ReentrantLock lock, DbEnum<PartOfSpeech> reg) {
		super(lock);
		this.reg = reg;
	}

	@Override
	protected void createTables() throws SQLException {
		connection.prepareStatement("create table if not exists PARTS_OF_SPEECH(id INTEGER, p_id INTEGER)").execute();
	}

	@Override
	protected List<PartOfSpeech> query(Kanji key) throws SQLException {
		if(!key.hasId())
			throw new IllegalArgumentException("id not set");

		PreparedStatement statement = connection.prepareStatement("select p_id from PARTS_OF_SPEECH where id=?");
		statement.setLong(1, key.id());
		ResultSet result = statement.executeQuery();

		List<PartOfSpeech> parts = new ArrayList<>(3);
		while(result.next()){
			long id = result.getInt(1);
			PartOfSpeech part = reg.select(id);
			if(part == null)
				throw new IllegalStateException(String.format("Invalid p_id (%d) in pronounciation for kanji %s (%d).", id, key, key.id()));
			parts.add(part);
		}

		return parts;
	}

	@Override
	protected void delete(Kanji key) throws SQLException {
		if(!key.hasId())
			throw new IllegalArgumentException("id not set");

		PreparedStatement statement = connection.prepareStatement("delete from PARTS_OF_SPEECH where id=?");
		statement.setLong(1, key.id());
		runLocking(statement::execute);
	}

	@Override
	protected void insert(Kanji key, List<PartOfSpeech> values) throws SQLException {
		if(!key.hasId())
			throw new IllegalArgumentException("id not set");

		PreparedStatement statement = connection.prepareStatement("insert into PARTS_OF_SPEECH (id, p_id) values (?, ?)");

		for (PartOfSpeech part : values) { // all of these should already have an ID
			statement.setLong(1, key.id());
			statement.setLong(2, part.id());
			statement.addBatch();
		}
		runLocking(statement::executeBatch);
	}
}