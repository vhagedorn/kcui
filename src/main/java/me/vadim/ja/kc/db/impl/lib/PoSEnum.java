package me.vadim.ja.kc.db.impl.lib;

import me.vadim.ja.kc.db.impl.DbEnumAdapter;
import me.vadim.ja.kc.wrapper.PartOfSpeech;

import java.sql.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author vadim
 */
class PoSEnum extends DbEnumAdapter<PartOfSpeech> {

	private static PartOfSpeech buildPartOfSpeech(ResultSet result, long id) throws SQLException {
		String name = result.getString(1);
		String info = result.getString(2);
		//wasNull is for primitives, but we'll include it just in case :)
		//value COALESCE'd to "null" in query statement
		info = info.equalsIgnoreCase("null") || result.wasNull() ? null : info;
		int prio = result.getInt(3);

		return PartOfSpeech.builder()
						   .name(name).info(info).priority(prio).id(id)
						   .build();
	}

	PoSEnum(ReentrantLock lock) {
		super(lock, PartOfSpeech[]::new);
	}

	@Override
	protected void createTables() throws SQLException {
		connection.prepareStatement("create table if not exists GRAMMAR(p_id INTEGER PRIMARY KEY, name TEXT, info TEXT, priority INT)").execute();
	}

	@Override
	protected void implDelete(long id) throws SQLException {
		PreparedStatement statement = connection.prepareStatement("delete from GRAMMAR where p_id=?");
		statement.setLong(1, id);
		runLocking(statement::execute);
	}

	@Override
	protected void implInsert(PartOfSpeech obj) throws SQLException {
		PreparedStatement statement = connection.prepareStatement("insert into GRAMMAR (name, info, priority) VALUES (?, ?, ?)");

		statement.setString(1, obj.name);
		if(obj.hasInfo())
			statement.setString(2, obj.info.value);
		else
			statement.setNull(2, Types.STRUCT);
		statement.setInt(3, obj.getPriority());
		runLocking(statement::execute);
		ResultSet result = statement.getGeneratedKeys();
		if (result.next()) {
			if (!obj.hasId()) // hehe thread safety go brr
				obj.setId(result.getLong(1));
		}
	}

	@Override
	protected void implUpdate(PartOfSpeech obj) throws SQLException {
		if (!obj.hasId())
			throw new IllegalArgumentException("id not set");

		PreparedStatement statement = connection.prepareStatement("update GRAMMAR SET name=?, info=?, priority=? WHERE p_id=?");
		statement.setString(1, obj.name);
		if(obj.hasInfo())
			statement.setString(2, obj.info.value);
		else
			statement.setNull(2, Types.STRUCT);
		statement.setInt(3, obj.getPriority());
		statement.setLong(4, obj.id());
		runLocking(statement::execute);
	}

	@Override
	protected boolean hasId(PartOfSpeech obj) {
		return obj.hasId();
	}

	@Override
	protected void setId(PartOfSpeech obj, long id) {
		obj.setId(id);
	}

	@Override
	protected long[] implSelect(PartOfSpeech obj) throws SQLException {
		PreparedStatement statement = connection.prepareStatement("select p_id from GRAMMAR where name=? AND priority=? AND " + (obj.hasInfo() ? "info=?" : "info IS NULL"));
		statement.setString(1, obj.name);
		statement.setInt(2, obj.getPriority());
		if (obj.hasInfo())
			statement.setString(3, obj.info.value);

		return parseKeySet(statement.executeQuery());
	}

	@Override
	protected PartOfSpeech implSelect(long id) throws SQLException {
		PreparedStatement statement = connection.prepareStatement("select name, COALESCE(info, 'null'), priority from GRAMMAR where p_id=?");
		statement.setLong(1, id);
		ResultSet result = statement.executeQuery();

		if (!result.next())
			return null;

		return buildPartOfSpeech(result, id);
	}

	@Override
	protected PreparedStatement selectAllQuery(boolean count) throws SQLException {
		return connection.prepareStatement(count
										   ? selectCountStatement("GRAMMAR")
										   : "select name, COALESCE(info, 'null'), priority, p_id from GRAMMAR");
	}

	@Override
	protected PartOfSpeech selectAllBuild(ResultSet result) throws SQLException {
		return buildPartOfSpeech(result, result.getLong(4));
	}


}
