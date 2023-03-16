package me.vadim.ja.kc.db.impl;

import me.vadim.ja.kc.db.JDBCDatabase;

/**
 * @author vadim
 */
public abstract class Sqlite3Database extends JDBCDatabase {

	public Sqlite3Database(String file) {
		super(new SQLite3Connector(file));
	}

}
