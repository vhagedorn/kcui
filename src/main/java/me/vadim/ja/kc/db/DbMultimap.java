package me.vadim.ja.kc.db;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * An interface to manage a map of {@link K objects} to a {@link List list} of {@link V values} stored in an SQL database.
 * @param <K> the keys used in this collection; will be mapped by {@link K#id() id}
 * @param <V> the values used in this collection; likely stored in a one-to-many relationship
 * @author vadim
 */
public interface DbMultimap<K extends Identifiable, V> extends DbAddon {

	/**
	 * Retrieve {@link V values} stored at a {@link K key}.
	 * @param key the {@link K object} whose {@link Identifiable#id() id} to search with
	 * @return a {@link List} of mapped {@link V values}
	 */
	@NotNull
	List<V> get(K key);

	/**
	 * Clear any entries mapped to the {@link K key}.
	 * @param key the {@link K object} whose {@link Identifiable#id() id} to search with
	 */
	void clear(K key);

	/**
	 * Map a {@link K key} to a {@link List list} of {@link V values}.
	 * <p>This <b>does not</b> {@link #clear(Identifiable) clear} any previously mapped values, but rather appends any new ones.
	 * @param key the {@link K object} whose {@link Identifiable#id() id} to map by
	 * @param value the {@link List list} of {@link V values} to map to the {@link K key}
	 */
	void put(K key, List<V> value);

}