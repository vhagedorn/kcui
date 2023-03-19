package me.vadim.ja.kc.db.impl.lib;

import me.vadim.ja.kc.db.impl.DbMultimapAdapter;
import me.vadim.ja.kc.wrapper.Definition;
import me.vadim.ja.kc.wrapper.Kanji;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author vadim
 */
class DefMultimap extends DbMultimapAdapter<Kanji, Definition> {

	@Override
	protected void createTables() throws SQLException {
		connection.prepareStatement("create table if not exists DEFINITIONS(id INTEGER, value TEXT, idx INT)").execute();
	}

	@Override
	protected List<Definition> query(Kanji key) throws SQLException {
		if(!key.hasId())
			throw new IllegalArgumentException("id not set");

		PreparedStatement statement = connection.prepareStatement("select value, idx from DEFINITIONS where id=?");
		statement.setLong(1, key.id());
		ResultSet result = statement.executeQuery();

		List<Definition> defs = new ArrayList<>(5);
		while(result.next())
			defs.add(Definition.builder().value(result.getString(1)).index(result.getInt(2)).build());

		return defs;
	}

	@Override
	protected void delete(Kanji key) throws SQLException {
		if(!key.hasId())
			throw new IllegalArgumentException("id not set");

		PreparedStatement statement = connection.prepareStatement("delete from DEFINITIONS where id=?");
		statement.setLong(1, key.id());
		statement.execute();
	}

	@Override
	protected void insert(Kanji key, List<Definition> values) throws SQLException {
		if(!key.hasId())
			throw new IllegalArgumentException("id not set");

		PreparedStatement statement = connection.prepareStatement("insert into DEFINITIONS (id, value, idx) values (?, ?, ?)");

		for (Definition def : values) {
			statement.setLong(1, key.id());
			statement.setString(2, def.value);
			statement.setInt(3, def.getIndex());
			statement.addBatch();
		}
		statement.executeBatch();
	}
}
