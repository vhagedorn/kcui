package me.vadim.ja.kc.db;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author vadim
 */
public interface JDBCConnector {

	Connection create() throws IOException, SQLException;

}
