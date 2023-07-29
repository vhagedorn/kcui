package me.vadim.ja.kc.db.impl.lib;

import me.vadim.ja.kc.db.impl.DbMultimapAdapter;
import me.vadim.ja.kc.wrapper.Kanji;
import me.vadim.ja.kc.wrapper.Pronounciation;
import me.vadim.ja.kc.persist.PronounciationType;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author vadim
 */
class PronMultimap extends DbMultimapAdapter<Kanji, Pronounciation> {

	PronMultimap(ReentrantLock lock) {
		super(lock);
	}

	@Override
	protected void createTables() throws SQLException {
		connection.prepareStatement("create table if not exists PRONOUNCIATIONS(id INTEGER, value TEXT, type INT, idx INT)").execute();
	}

	@Override
	protected List<Pronounciation> query(Kanji key) throws SQLException {
		if(!key.hasId())
			throw new IllegalArgumentException("id not set");

		PreparedStatement statement = connection.prepareStatement("select value, type, idx from PRONOUNCIATIONS where id=?");
		statement.setLong(1, key.id());
		ResultSet result = statement.executeQuery();

		List<Pronounciation> prons = new ArrayList<>(5);
		while(result.next()) {
			int id = result.getInt(2);
			PronounciationType type = PronounciationType.fromID(id);
			if(type == null)
				throw new IllegalStateException(String.format("Invalid type (%d) in pronounciation for kanji %s (%d).", id, key, key.id()));
			prons.add(Pronounciation.builder().value(result.getString(1)).type(type).index(result.getInt(3)).build());
		}
		return prons;
	}

	@Override
	protected void delete(Kanji key) throws SQLException {
		if(!key.hasId())
			throw new IllegalArgumentException("id not set");

		PreparedStatement statement = connection.prepareStatement("delete from PRONOUNCIATIONS where id=?");
		statement.setLong(1, key.id());
		runLocking(statement::execute);
	}

	@Override
	protected void insert(Kanji key, List<Pronounciation> values) throws SQLException {
		if(!key.hasId())
			throw new IllegalArgumentException("id not set");

		PreparedStatement statement = connection.prepareStatement("insert into PRONOUNCIATIONS (id, value, type, idx) values (?, ?, ?, ?)");

		for (Pronounciation pron : values) {
			statement.setLong(1, key.id());
			statement.setString(2, pron.value);
			statement.setInt(3, pron.type.id);
			statement.setInt(4, pron.getIndex());
			statement.addBatch();
		}
		runLocking(statement::executeBatch);
	}
}
