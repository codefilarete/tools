package org.gama.lang.sql;

import java.sql.Connection;
import java.sql.SQLException;

import org.gama.lang.function.ThrowingConsumer;

/**
 * Class aimed at easing creating, commiting and rolbacking q SQL Transaction
 * 
 * @author Guillaume Mary
 */
public class TransactionSupport {
	
	/**
	 * Simple method to apply any sql order (through a {@link Connection} {@link java.util.function.Consumer}) within a transaction.
	 * Statements must be created by the {@link java.util.function.Consumer} from the {@link Connection}, if they are created outside then
	 * behavior is unknown (can't find any information about calling {@link Connection#createStatement()} before disabling commit and then
	 * executing statement).
	 * 
	 * @param connectionConsumer a sql order cerator and executor
	 * @param connection the connection that will manage the transaction (will by given to {@link java.util.function.Consumer} as argument)
	 * @throws SQLException any error thrown during connection interaction
	 */
	public static void runAtomically(ThrowingConsumer<Connection, SQLException> connectionConsumer, Connection connection) throws SQLException {
		new TransactionSupport(connection)
				.begin()
				.runAtomically(connectionConsumer)
				.end();
	}
	
	/** Transaction identifier. For logging or any other tracing process */
	private final String transactionId;
	
	private final Connection connection;
	private boolean wasAutoCommit;
	
	public TransactionSupport(Connection connection) {
		this.connection = connection;
		// in a non JVM-shared, an Hexa String of the hashCode is sufficient as id
		transactionId = Integer.toHexString(hashCode());
	}
	
	public String getId() {
		return transactionId;
	}
	
	public TransactionSupport begin() throws SQLException {
		// remember previous commit mode to set it back further
		wasAutoCommit = connection.getAutoCommit();
		connection.setAutoCommit(false);
		return this;
	}
	
	public void commit() throws SQLException {
		connection.commit();
	}
	
	public void rollback() throws SQLException {
		connection.rollback();
	}
	
	public TransactionSupport end() throws SQLException {
		// set commit state back
		connection.setAutoCommit(wasAutoCommit);
		return this;
	}
	
	/**
	 * Will execute any sql order (through a {@link Connection} {@link java.util.function.Consumer}) within a transaction.
	 * @param connectionConsumer a sql order cerator and executor
	 * @return this
	 * @throws SQLException any error thrown during connection interaction
	 */
	public TransactionSupport runAtomically(ThrowingConsumer<Connection, SQLException> connectionConsumer) throws SQLException {
		begin();
		try {
			connectionConsumer.accept(connection);
			commit();
		} catch (SQLException e) {
			try {
				rollback();
			} catch (SQLException e1) {
				e.addSuppressed(e1);
			}
			throw e;
		} finally {
			end();
		}
		return this;
	}
}
