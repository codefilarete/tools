package org.gama.lang.sql;

import java.sql.Connection;
import java.sql.SQLException;

import org.gama.lang.trace.ModifiableBoolean;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

/**
 * @author Guillaume Mary
 */
@RunWith(MockitoJUnitRunner.class)
public class TransactionSupportTest {
	
	@Test
	public void testCommitStateReversion_true() throws SQLException {
		ModifiableBoolean commitState = new ModifiableBoolean(true);
		
		TransactionSupport testInstance = new TransactionSupport(new ConnectionWrapper() {
			@Override
			public boolean getAutoCommit() {
				return commitState.getValue();
			}
			
			@Override
			public void setAutoCommit(boolean autoCommit) {
				commitState.setValue(autoCommit);
			}
		});
		
		// begin() must set auto-commit to false
		testInstance.begin();
		assertFalse(commitState.getValue());
		
		// end() must set auto-commit back to true
		testInstance.end();
		assertTrue(commitState.getValue());
	}
	
	@Test
	public void testCommitStateReversion_false() throws SQLException {
		ModifiableBoolean commitState = new ModifiableBoolean(false);
		
		TransactionSupport testInstance = new TransactionSupport(new ConnectionWrapper() {
			@Override
			public boolean getAutoCommit() {
				return commitState.getValue();
			}
			
			@Override
			public void setAutoCommit(boolean autoCommit) {
				commitState.setValue(autoCommit);
			}
		});
		
		// begin() must set auto-commit to false
		testInstance.begin();
		assertFalse(commitState.getValue());
		
		// end() must set auto-commit back to true
		testInstance.end();
		assertFalse(commitState.getValue());
	}
	
	@Test
	public void testBegin_connectionAutoCommitIsSetToFalse() throws SQLException {
		Connection mock = mock(Connection.class);
		TransactionSupport testInstance = new TransactionSupport(mock);
		testInstance.begin();
		
		verify(mock).setAutoCommit(eq(false));
	}
	
	@Test
	public void testEnd_connectionAutoCommitIsCalled() throws SQLException {
		Connection connectionMock = mock(Connection.class);
		TransactionSupport testInstance = new TransactionSupport(connectionMock);
		testInstance.begin();
		
		verify(connectionMock).setAutoCommit(anyBoolean());
	}
	
	@Test
	public void testRunAtomically_normalCase() throws SQLException {
		Connection connectionMock = mock(Connection.class);
		TransactionSupport testInstance = new TransactionSupport(connectionMock);
		TransactionSupport spy = spy(testInstance);
		
		Object[] consumerState = new Object[1];
		spy.runAtomically(c -> consumerState[0] = c);
		
		verify(spy).commit();
		verify(connectionMock).commit();
		verify(spy).begin();
		assertSame(connectionMock, consumerState[0]);
		verify(spy).end();
	}
	
	@Test
	public void testRunAtomically_rollbackOnException() throws SQLException {
		Connection connectionMock = mock(Connection.class);
		TransactionSupport testInstance = new TransactionSupport(connectionMock);
		TransactionSupport spy = spy(testInstance);
		
		SQLException caughtException = null;
		try {
			spy.runAtomically(c -> {
				throw new SQLException();
			});
		} catch (SQLException e) {
			caughtException = e;
		}
		assertNotNull(caughtException);
		
		verify(spy, never()).commit();
		verify(connectionMock, never()).commit();
		verify(spy).begin();
		verify(spy).rollback();
		verify(spy).end();
	}
}