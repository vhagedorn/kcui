package me.vadim.ja.kc.db;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;

/**
 * @author vadim
 */
public interface DatabaseSession extends AutoCloseable {

	ConnectionSource getConnectionSource();

	<T, I> Dao<T, I> getDAO(Class<T> objClass, Class<I> idClass);

	@Override
	void close();

}