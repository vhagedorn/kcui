package me.vadim.ja.kc.db.impl;

import me.vadim.ja.kc.db.DbAddon;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author vadim
 */
public abstract class DbAddonAdapter implements DbAddon {

	protected Connection connection;

	@Override
	public final void initialize(Connection connection) {
		this.connection = connection;
		try {
			createTables();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	protected abstract void createTables() throws SQLException;

}
