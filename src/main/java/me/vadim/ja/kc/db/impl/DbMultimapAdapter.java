package me.vadim.ja.kc.db.impl;

import me.vadim.ja.kc.db.DbMultimap;
import me.vadim.ja.kc.db.Identifiable;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author vadim
 */
@Deprecated
public abstract class DbMultimapAdapter<K extends Identifiable, V> extends DbAddonAdapter implements DbMultimap<K, V> {

	public DbMultimapAdapter() {
		super();
	}

	public DbMultimapAdapter(ReentrantLock lock) {
		super(lock);
	}

	@NotNull
	@Override
	public final List<V> get(K key) {
		try {
			return query(key);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	protected abstract List<V> query(K key) throws SQLException;

	@Override
	public void clear(K key) {
		try {
			delete(key);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	protected abstract void delete(K key) throws SQLException;

	@Override
	public void put(K key, List<V> value) {
		try {
			insert(key, value);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	protected abstract void insert(K key, List<V> values) throws SQLException;

}
