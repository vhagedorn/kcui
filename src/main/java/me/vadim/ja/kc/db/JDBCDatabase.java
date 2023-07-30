package me.vadim.ja.kc.db;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author vadim
 */
@Deprecated
public abstract class JDBCDatabase {

	protected Connection connection;
	private final JDBCConnector connector;

	public JDBCDatabase(JDBCConnector connector) {
		this.connector = connector;
	}

	public void connect() {
		try {
			preConnect();
			connection = connector.create();
			onConnect();
		} catch (SQLException | IOException x) {
			throw new RuntimeException(x);
		}
	}

	protected void preConnect() /* connection not yet created */ { }

	protected void onConnect() throws SQLException { }

	public void disconnect() {
		try {
			preDisconnect();
			connection.close();
			connection = null;
			onDisconnect();
		} catch (SQLException x) {
			throw new RuntimeException(x);
		}
	}

	protected void preDisconnect() throws SQLException { }

	protected void onDisconnect()  /* connection already disposed */ { }

	public boolean isConnected() {
		try {
			return connection != null && !connection.isClosed();
		} catch (SQLException e) {
			return false;
		}
	}

}
