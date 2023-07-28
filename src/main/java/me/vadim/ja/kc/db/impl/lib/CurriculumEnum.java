package me.vadim.ja.kc.db.impl.lib;

import me.vadim.ja.kc.db.DbEnum;
import me.vadim.ja.kc.db.impl.DbEnumAdapter;
import me.vadim.ja.kc.wrapper.Curriculum;
import me.vadim.ja.kc.wrapper.Group;
import me.vadim.ja.kc.wrapper.IdAdapter;
import me.vadim.ja.kc.wrapper.Identifiable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * @author vadim
 */
class CurriculumEnum extends DbEnumAdapter<Curriculum> {

	private /*static*/ Curriculum create(String name, long id) {
		Curriculum c = new Curriculum(name);
		c.setId(id);
		for (Group.Builder group : groups.values())
			if (group.c_id == id)
				c.groups.add(group.curriculum(c).build());

		return c;
	}

	private void clearGroups(Curriculum curriculum) {
		if(!curriculum.hasId()) return;
		List<Long> ids = curriculum.groups.stream().filter(IdAdapter::hasId).map(Identifiable::id).collect(Collectors.toList());
		List<Long> buf = new ArrayList<>();
		for (Group.Builder val : groups.values()) // filter groups in the db which weren't passed to this method (they need to be deleted)
			if (!ids.contains(val.id) && val.c_id == curriculum.id())
				buf.add(val.id);
		buf.forEach(groups::delete);
	}

	private final DbEnum<Group.Builder> groups;

	CurriculumEnum(ReentrantLock lock) {
		super(lock, Curriculum[]::new);
		this.groups = new GBEnum(lock);
	}

	@Override
	protected void createTables() throws SQLException {
		connection.prepareStatement("create table if not exists CURRICULUMS(c_id INTEGER PRIMARY KEY, name TEXT)").execute();
		groups.initialize(connection);
	}

	@Override
	protected void implDelete(long id) throws SQLException {
		Curriculum q = select(id);
		if (q != null)
			q.groups.stream().map(Group::id).forEach(groups::delete);

		PreparedStatement statement = connection.prepareStatement("delete from CURRICULUMS where c_id=?");
		statement.setLong(1, id);
		runLocking(statement::execute);
	}

	@Override
	protected void implInsert(Curriculum obj) throws SQLException {
		PreparedStatement statement = connection.prepareStatement("insert into CURRICULUMS (name) VALUES (?)");

		statement.setString(1, obj.getName());
		runLocking(statement::execute);
		ResultSet result = statement.getGeneratedKeys();
		if (result.next()) {
			if (!obj.hasId()) // hehe thread safety go brr
				obj.setId(result.getLong(1));
		}

		for (Group group : obj.groups) {
			Group.Builder cp = group.copy();
			clearGroups(obj);
			groups.update(cp);
			if (!group.hasId())
				group.setId(cp.id);
		}
	}

	@Override
	protected void implUpdate(Curriculum obj) throws SQLException {
		if (!obj.hasId())
			throw new IllegalArgumentException("id not set");

		PreparedStatement statement = connection.prepareStatement("update CURRICULUMS SET name=? WHERE c_id=?");
		statement.setString(1, obj.getName());
		statement.setLong(2, obj.id());
		runLocking(statement::execute);

		for (Group group : obj.groups) {
			Group.Builder cp = group.copy();
			if (group.hasId())
				cp.id(group.id());

			clearGroups(obj);
			groups.update(cp);

			if (!group.hasId())
				group.setId(cp.id);
		}
	}

	@Override
	protected boolean hasId(Curriculum obj) {
		return obj.hasId();
	}

	@Override
	protected void setId(Curriculum obj, long id) {
		obj.setId(id);
	}

	@Override
	protected long[] implSelect(Curriculum obj) throws SQLException {
		PreparedStatement statement = connection.prepareStatement("select c_id from CURRICULUMS where name=?");
		statement.setString(1, obj.getName());

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