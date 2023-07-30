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
@Deprecated
public class SQLite3Connector implements JDBCConnector {

	private final File db;

	public SQLite3Connector(String file) {
		this(new File(file));
	}

	public SQLite3Connector(File file) {
		this.db = file;
	}

	@SuppressWarnings("ResultOfMethodCallIgnored")
	@Override
	public Connection create() throws IOException, SQLException {
		try {
			if (!db.exists()) {
				File parent = db.getParentFile();
				if (parent != null)
					parent.mkdirs();
				db.createNewFile();
			}
		} catch (IOException x) {
			x.printStackTrace();
		}
		if (!db.isFile())
			throw new RuntimeException("Unable to access db file '" + db.getPath() + "'.");

		return DriverManager.getConnection("jdbc:sqlite:" + db.getPath());
	}

}
