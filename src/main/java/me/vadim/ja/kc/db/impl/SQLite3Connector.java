package me.vadim.ja.kc.db.impl;

import me.vadim.ja.kc.db.JDBCConnector;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author vadim
 */
public class SQLite3Connector implements JDBCConnector {

	private final File db;

	public SQLite3Connector(String file) {
		this(new File(file));
	}

	@SuppressWarnings("ResultOfMethodCallIgnored")
	public SQLite3Connector(File file) {
		try {
			if (!file.exists()) {
				File parent = file.getParentFile();
				if(parent != null)
					parent.mkdirs();
				file.createNewFile();
			}
		} catch (IOException x) {
			x.printStackTrace();
		}
		if (!file.isFile())
			throw new RuntimeException("Unable to access db file '" + file.getPath() + "'.");

		this.db = file;
	}

	@Override
	public Connection create() throws IOException, SQLException {
		return DriverManager.getConnection("jdbc:sqlite:" + db.getPath());
	}
}
