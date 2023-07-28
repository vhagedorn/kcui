package me.vadim.ja.kc.db.impl;

import me.vadim.ja.kc.db.DbEnum;
import me.vadim.ja.kc.wrapper.IdCloneable;
import org.jetbrains.annotations.NotNull;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.IntFunction;

/**
 * @author vadim
 */
public abstract class DbEnumAdapter<I extends IdCloneable<I>> extends DbAddonAdapter implements DbEnum<I> {

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
		super();
		this.factory = factory;
	}

	public DbEnumAdapter(ReentrantLock lock, IntFunction<I[]> factory) {
		super(lock);
		this.factory = factory;
	}

	/* actual impls */

	protected abstract void implDelete(long id) throws SQLException;
	protected abstract void implInsert(I obj) throws SQLException;
	protected abstract void implUpdate(I obj) throws SQLException;
	protected abstract long[] implSelect(I obj) throws SQLException;
	protected abstract I implSelect(long id) throws SQLException;

	/* helper methods for this class */

	protected abstract boolean hasId(I obj);
	protected abstract void setId(I obj, long id);
	protected abstract PreparedStatement selectAllQuery(boolean count) throws SQLException;
	protected abstract I selectAllBuild(ResultSet result) throws SQLException;

	@Override
	public final void delete(long id) {
		try {
			executeLocking(() -> implDelete(id));
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public final void create(I obj) {
		try {
			executeLocking(() -> implInsert(obj));
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void update(I obj) {
		long   id  = hasId(obj) ? obj.id() : -1;
		long[] ids = findSimilar(obj);
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

		try {
			if (id == -1) // INSERT new row
				executeLocking(() -> implInsert(obj));
			else {// UPDATE existing row
				if(!hasId(obj)) // using withId will not fix original object's id. plus I don't want to modify withId to NOT return a clone...
					setId(obj, id);
				final long _id = id;
				executeLocking(() -> implUpdate(obj.withId(_id))); // use found ID instead
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@NotNull
	@Override
	public final long[] findSimilar(I obj) {
		try {
			return implSelect(obj);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public final I select(long id) {
		try {
			return implSelect(id);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@NotNull
	@Override
	public I[] values() {
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

}
