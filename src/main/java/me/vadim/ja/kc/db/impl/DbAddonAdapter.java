package me.vadim.ja.kc.db.impl;

import me.vadim.ja.kc.db.DbAddon;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author vadim
 */
public abstract class DbAddonAdapter implements DbAddon {

	protected Connection connection;
	protected final ReentrantLock lock; // primarily utilized for lock-on-write operations

	public DbAddonAdapter(){
		this(null);
	}

	public DbAddonAdapter(ReentrantLock lock) {
		this.lock = lock;
	}

	@Override
	public final void initialize(Connection connection) {
		this.connection = connection;
		try {
			createTables();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@FunctionalInterface
	public interface SQLExceptional {
		void run() throws SQLException;
	}

	/**
	 * Run a block, synchronized on the {@link #lock} field.
	 * Wraps any {@link SQLException} in a {@link RuntimeException} and rethrows.
	 * @param statement the lambda to execute
	 * @see #executeLocking(SQLExceptional)
	 */
	protected final void runLocking(SQLExceptional statement) {
		Exception rethrow = null;
		try {
			executeLocking(statement);
		} catch (SQLException e) {
			rethrow = e;
		}
		if(rethrow != null)
			throw new RuntimeException(rethrow);
	}

	/**
	 * Run a block, synchronized on the {@link #lock} field.
	 * Does not catch exceptions.
	 * @param statement the lambda to execute   
	 */
	protected final void executeLocking(SQLExceptional statement) throws SQLException {
		if(lock == null)
			throw new NullPointerException("Use `super(lock);` if you want to use `runLocking(...);`");
		lock.lock();
		try {
			statement.run();
		} finally {
			lock.unlock();
		}
	}

	protected abstract void createTables() throws SQLException;

}
