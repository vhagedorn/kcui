package me.vadim.ja.kc.db.impl;

import com.j256.ormlite.jdbc.JdbcConnectionSource;

import java.io.File;
import java.sql.SQLException;

/**
 * @author vadim
 */
public class SQLiteSession extends AbstractSession {

	public SQLiteSession(File db) throws SQLException {
		super(new JdbcConnectionSource(String.format("jdbc:sqlite:%s", db.getAbsolutePath())));
	}

}
