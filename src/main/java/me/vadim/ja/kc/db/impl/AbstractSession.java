package me.vadim.ja.kc.db.impl;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.support.DatabaseConnection;
import com.j256.ormlite.table.TableUtils;
import me.vadim.ja.kc.db.DatabaseSession;
import me.vadim.ja.kc.util.Util;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author vadim
 */
public abstract class AbstractSession implements DatabaseSession {

	private final ConnectionSource source;

	public AbstractSession(ConnectionSource source) {
		this.source = source;
	}

	@Override
	public final ConnectionSource getConnectionSource() {
		return source;
	}

	private final List<Dao<?, ?>> daos = new ArrayList<>();

	private final void table(Dao<?, ?> dao) throws SQLException {
		TableUtils.createTableIfNotExists(source, dao.getDataClass());
	}

	@Override
	public <T, I> Dao<T, I> getDAO(Class<T> objClass, Class<I> idClass) {
		try {
			Dao<T, I> dao = DaoManager.createDao(source, objClass);
			table(dao);
			daos.add(dao);
			return dao;
		} catch (SQLException x) {
			x.printStackTrace();
			Util.sneaky(x);
			return null; // never happens
		}
	}

	@Override
	public final void close() {
		try {
			for (Dao<?, ?> dao : daos)
				try (DatabaseConnection connection = source.getReadWriteConnection(dao.getTableName())) {
					table(dao);
					connection.setAutoCommit(false);
					dao.commit(connection);
				} catch (SQLException x) {
					x.printStackTrace();
					Util.sneaky(x);
				}

			source.close();
		} catch (Exception x) {
			x.printStackTrace();
			Util.sneaky(x);
		}
	}

}
