package me.vadim.ja.kc.db.impl.lib;

import me.vadim.ja.kc.db.impl.DbEnumAdapter;
import me.vadim.ja.kc.wrapper.Group;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author vadim
 */
class GBEnum extends DbEnumAdapter<Group.Builder> {

	private static Group.Builder create(ResultSet result, long id) throws SQLException {
		return Group.builder().name(result.getString(1)).curriculum(result.getLong(2)).id(id);
	}

	GBEnum() {
		super(Group.Builder[]::new);
	}

	@Override
	protected void createTables() throws SQLException {
		connection.prepareStatement("create table if not exists GROUPS(g_id INTEGER PRIMARY KEY, c_id INTEGER, name TEXT)").execute();
	}

	@Override
	protected void implDelete(long id) throws SQLException {
		PreparedStatement statement = connection.prepareStatement("delete from GROUPS where g_id=?");
		statement.setLong(1, id);
		statement.execute();
	}

	@Override
	protected void implInsert(Group.Builder obj) throws SQLException {
		PreparedStatement statement = connection.prepareStatement("insert into GROUPS (name, c_id) VALUES (?, ?)");

		statement.setString(1, obj.name);
		statement.setLong(2, obj.curriculum.id());
		statement.execute();
		ResultSet result = statement.getGeneratedKeys();
		if (result.next()) {
			obj.id(result.getLong(1));
		}
	}

	@Override
	protected void implUpdate(Group.Builder obj) throws SQLException {
		if (!hasId(obj))
			throw new IllegalArgumentException("id not set");

		PreparedStatement statement = connection.prepareStatement("update GROUPS SET name=?, c_id=? WHERE g_id=?");
		statement.setString(1, obj.name);
		statement.setLong(2, obj.curriculum.id());
		statement.setLong(3, obj.id);
		statement.execute();
	}


	@Override
	protected boolean hasId(Group.Builder obj) {
		return obj.id != -1;
	}

	@Override
	protected void setId(Group.Builder obj, long id) {
		obj.id(id);
	}

	@Override
	protected long[] implSelect(Group.Builder obj) throws SQLException {
		PreparedStatement statement = connection.prepareStatement("select g_id from GROUPS where name=? AND c_id=?");
		statement.setString(1, obj.name);
		statement.setLong(2, obj.curriculum.id());

		return parseKeySet(statement.executeQuery());
	}

	@Override
	protected Group.Builder implSelect(long id) throws SQLException {
		PreparedStatement statement = connection.prepareStatement("select name, c_id from GROUPS where g_id=?");
		statement.setLong(1, id);
		ResultSet result = statement.executeQuery();

		if (!result.next())
			return null;

		return create(result, id);
	}

	@Override
	protected PreparedStatement selectAllQuery(boolean count) throws SQLException {
		return connection.prepareStatement(count
										   ? selectCountStatement("GROUPS")
										   : "select name, c_id, g_id from GROUPS");
	}

	@Override
	protected Group.Builder selectAllBuild(ResultSet result) throws SQLException {
		return create(result, result.getLong(3));
	}

}