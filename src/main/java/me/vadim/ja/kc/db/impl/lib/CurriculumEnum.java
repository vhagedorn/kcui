package me.vadim.ja.kc.db.impl.lib;

import me.vadim.ja.kc.db.DbEnum;
import me.vadim.ja.kc.db.impl.DbEnumAdapter;
import me.vadim.ja.kc.wrapper.Curriculum;
import me.vadim.ja.kc.wrapper.Group;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author vadim
 */
class CurriculumEnum extends DbEnumAdapter<Curriculum> {

	private /*static*/ Curriculum create(String name, long id){
		Curriculum c = new Curriculum(name);
		c.setId(id);
		for (Group.Builder group : groups.values())
			if(group.c_id == id)
				c.groups.add(group.curriculum(c).build());

		return c;
	}

	private final DbEnum<Group.Builder> groups;

	CurriculumEnum() {
		super(Curriculum[]::new);
		this.groups = new GBEnum();
	}

	@Override
	protected void createTables() throws SQLException {
		connection.prepareStatement("create table if not exists CURRICULUMS(c_id INTEGER PRIMARY KEY, name TEXT)").execute();
		groups.initialize(connection);
	}

	@Override
	protected void implDelete(long id) throws SQLException {
		Curriculum q = select(id);
		if(q != null)
			q.groups.stream().map(Group::id).forEach(groups::delete);

		PreparedStatement statement = connection.prepareStatement("delete from CURRICULUMS where c_id=?");
		statement.setLong(1, id);
		statement.execute();
	}

	@Override
	protected void implInsert(Curriculum obj) throws SQLException {
		PreparedStatement statement = connection.prepareStatement("insert into CURRICULUMS (name) VALUES (?)");

		statement.setString(1, obj.name);
		statement.execute();
		ResultSet result = statement.getGeneratedKeys();
		if (result.next()) {
			if (!obj.isIdSet()) // hehe thread safety go brr
				obj.setId(result.getLong(1));
		}

		for (Group group : obj.groups) {
			Group.Builder cp = group.copy();
			groups.create(cp);
			if(!group.isIdSet())
				group.setId(cp.id);
		}
	}

	@Override
	protected void implUpdate(Curriculum obj) throws SQLException {
		if (!obj.isIdSet())
			throw new IllegalArgumentException("id not set");

		PreparedStatement statement = connection.prepareStatement("update CURRICULUMS SET name=? WHERE c_id=?");
		statement.setString(1, obj.name);
		statement.setLong(2, obj.id());
		statement.execute();

		for (Group group : obj.groups) {
			Group.Builder cp = group.copy();
			if(group.isIdSet())
				cp.id(group.id());

			groups.update(cp);

			if(!group.isIdSet())
				group.setId(cp.id);
		}
	}

	@Override
	protected Curriculum withId(Curriculum obj, long newId) {
		return create(obj.name, newId);
	}

	@Override
	protected boolean hasId(Curriculum obj) {
		return obj.isIdSet();
	}

	@Override
	protected long[] implSelect(Curriculum obj) throws SQLException {
		PreparedStatement statement = connection.prepareStatement("select c_id from CURRICULUMS where name=?");
		statement.setString(1, obj.name);
		
		return parseKeySet(statement.executeQuery());
	}

	@Override
	protected Curriculum implSelect(long id) throws SQLException {
		PreparedStatement statement = connection.prepareStatement("select name from CURRICULUMS where c_id=?");
		statement.setLong(1, id);
		ResultSet result = statement.executeQuery();

		if (!result.next())
			return null;

		return create(result.getString(1), id);
	}

	@Override
	protected PreparedStatement selectAllQuery(boolean count) throws SQLException {
		return connection.prepareStatement(count
										   ? selectCountStatement("CURRICULUMS")
										   : "select name, c_id from CURRICULUMS");
	}

	@Override
	protected Curriculum selectAllBuild(ResultSet result) throws SQLException {
		return create(result.getString(1), result.getLong(2));
	}

}