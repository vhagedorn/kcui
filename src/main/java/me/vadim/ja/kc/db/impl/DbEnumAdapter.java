package me.vadim.ja.kc.db.impl;

import me.vadim.ja.kc.db.DbEnum;
import me.vadim.ja.kc.wrapper.Identifiable;
import org.jetbrains.annotations.NotNull;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.IntFunction;

/**
 * @author vadim
 */
public abstract class DbEnumAdapter<I extends Identifiable> extends DbAddonAdapter implements DbEnum<I> {

	protected static long[] parseKeySet(ResultSet result) throws SQLException {
		List<Long> longList = new ArrayList<>();
		while (result.next())
			longList.add(result.getLong(1));

		return longList.stream().sorted().mapToLong(Long::longValue).toArray();
	}

	protected static String selectCountStatement(String tableName){
		return "select Count(*) from " + tableName;
	}

	private final IntFunction<I[]> factory;

	public DbEnumAdapter(IntFunction<I[]> factory) {
		this.factory = factory;
	}

	@Override
	public final void delete(long id) {
		try {
			implDelete(id);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	protected abstract void implDelete(long id) throws SQLException;

	@Override
	public final void insert(I obj) {
		try {
			implInsert(obj);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	protected abstract void implInsert(I obj) throws SQLException;

	@Override
	public final void update(I obj) {
		try {
			implUpdate(obj);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	protected abstract void implUpdate(I obj) throws SQLException;

	@Override
	public void upsert(I obj) {
		long   id  = hasId(obj) ? obj.id() : -1;
		long[] ids = query(obj);
		if (hasId(obj)) { // DELETE other entries
			for (long l : ids)
				if (l != id)
					delete(l);
		} else if (ids.length > 1) { // DELETE excess duplicates
			for (int i = 0; i < ids.length - 1; i++)
				 delete(ids[i]);
			id = ids[ids.length - 1];
		} else if (ids.length == 1)
			id = ids[0];

		if (id == -1) // INSERT new row
			insert(obj);
		else // UPDATE existing row
			update(withId(obj, id)); // use found ID instead
	}

	protected abstract I withId(I obj, long newId);
	protected abstract boolean hasId(I obj);

	@NotNull
	@Override
	public final long[] query(I obj) {
		try {
			return findSimilar(obj);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	protected abstract long[] findSimilar(I obj) throws SQLException;

	@Override
	public final I query(long id) {
		try {
			return getByID(id);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	protected abstract I getByID(long id) throws SQLException;

	@NotNull
	@Override
	public I[] query() {
		try {
			PreparedStatement statement;
			ResultSet             result;

			statement = selectAllQuery(true);
			result    = statement.executeQuery();
			if (!result.next())
				throw new IllegalStateException("unable to count rows");
			int ct = result.getInt(1);

			statement = selectAllQuery(false);
			result    = statement.executeQuery();
			I[] buf = factory.apply(ct);
			int            i   = 0;
			while (result.next())
				buf[i++] = selectAllBuild(result);

			return buf;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	protected abstract PreparedStatement selectAllQuery(boolean count) throws SQLException;
	protected abstract I selectAllBuild(ResultSet result) throws SQLException;

}
