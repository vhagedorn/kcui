package me.vadim.ja.kc.db;

import java.sql.SQLException;

/**
 * @author vadim
 */
@FunctionalInterface
public interface SessionProvider {

	DatabaseSession session() throws SQLException;

}
