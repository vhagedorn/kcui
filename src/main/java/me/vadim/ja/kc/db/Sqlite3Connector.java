package me.vadim.ja.kc.db;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author vadim
 */
public abstract class Sqlite3Connector {

	protected Connection connection;

	private final File db;

	public Sqlite3Connector(String file) {
		this(new File(file));
	}

	@SuppressWarnings("ResultOfMethodCallIgnored")
	public Sqlite3Connector(File file) {
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

	public final void connect(){
		try {
			preConnect();
			connection = DriverManager.getConnection("jdbc:sqlite:" + db.getPath());
			onConnect();
			createTables();
		} catch (SQLException x) {
			throw new RuntimeException(x);
		}
	}

	protected void preConnect() /* connection not yet created */ {}
	protected void onConnect() throws SQLException {}

	public final void disconnect() {
		try {
			preDisconnect();
			connection.close();
			onDisconnect();
		} catch (SQLException x){
			throw new RuntimeException(x);
		}
	}

	protected void preDisconnect() throws SQLException {}
	protected void onDisconnect()  /* connection already disposed */ {}

	protected void createTables() throws SQLException {}

}
