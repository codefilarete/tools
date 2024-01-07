package org.codefilarete.tool.sql;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

import org.codefilarete.tool.Reflections;

/**
 * {@link DataSource} that wraps another one and delegates all its methods to it without any additionnal feature.
 * Made for overriding only some targeted methods.
 *
 * @author Guillaume Mary
 */
public class DataSourceWrapper implements DataSource {
	
	private DataSource delegate;
	
	public DataSourceWrapper() {
	}
	
	public DataSourceWrapper(DataSource delegate) {
		this.delegate = delegate;
	}
	
	public void setDelegate(DataSource delegate) {
		this.delegate = delegate;
	}
	
	@Override
	public Connection getConnection() throws SQLException {
		return delegate.getConnection();
	}
	
	@Override
	public Connection getConnection(String username, String password) throws SQLException {
		return delegate.getConnection(username, password);
	}
	
	@Override
	public PrintWriter getLogWriter() throws SQLException {
		return delegate.getLogWriter();
	}
	
	@Override
	public void setLogWriter(PrintWriter out) throws SQLException {
		delegate.setLogWriter(out);
	}
	
	@Override
	public void setLoginTimeout(int seconds) throws SQLException {
		delegate.setLoginTimeout(seconds);
	}
	
	@Override
	public int getLoginTimeout() throws SQLException {
		return delegate.getLoginTimeout();
	}
	
	@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		return delegate.getParentLogger();
	}
	
	/**
	 * Returns delegate instance if it is type matches with given one, else asks for unwrapping itself.
	 *
	 * @param iface an interface that the result must implement
	 * @return an object that implements given interface
	 * @param <T> the type of the class modeled by this Class object
	 * @throws SQLException if no object found that implements the interface
	 */
	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		if (iface.isInstance(this.delegate)) {
			return (T) this.delegate;
		} else if (this.delegate != null) {
			return this.delegate.unwrap(iface);
		} else {
			throw new SQLException(Reflections.toString(DataSourceWrapper.class) + " cannot be unwrapped as " + Reflections.toString(iface));
		}
	}
	
	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return iface.isInstance(this.delegate) || this.delegate != null && this.delegate.isWrapperFor(iface);
	}
}
