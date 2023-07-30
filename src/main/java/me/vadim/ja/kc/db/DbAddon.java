package me.vadim.ja.kc.db;

import java.sql.Connection;

/**
 * A structured addition to an SQL database.
 *
 * @author vadim
 */
@Deprecated
public interface DbAddon {

	/**
	 * Initialize the database by creating any necessary tables.
	 *
	 * @param connection the {@link Connection} to the database (it will be cached and reused)
	 */
	void initialize(Connection connection);

}
