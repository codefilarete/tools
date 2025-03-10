package org.codefilarete.tool.sql;

import java.sql.Connection;
import java.sql.SQLException;

import org.codefilarete.tool.function.ThrowingConsumer;
import org.codefilarete.tool.trace.MutableBoolean;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Guillaume Mary
 */
class TransactionSupportTest {
	
	@Test
	void commitStateReversion_true() throws SQLException {
		MutableBoolean commitState = new MutableBoolean(true);
		
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
		assertThat(commitState.getValue()).isFalse();
		
		// end() must set auto-commit back to true
		testInstance.end();
		assertThat(commitState.getValue()).isTrue();
	}
	
	@Test
	void commitStateReversion_false() throws SQLException {
		MutableBoolean commitState = new MutableBoolean(false);
		
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
		assertThat(commitState.getValue()).isFalse();
		
		// end() must set auto-commit back to true
		testInstance.end();
		assertThat(commitState.getValue()).isFalse();
	}
	
	@Test
	void end_connectionAutoCommitIsCalledIfNecessary() throws SQLException {
		Connection connectionMock = mock(Connection.class);
		TransactionSupport testInstance = new TransactionSupport(connectionMock);
		testInstance.begin();
		
		verify(connectionMock, times(0)).setAutoCommit(anyBoolean());
		
		when(connectionMock.getAutoCommit()).thenReturn(true);
		testInstance.begin();
		verify(connectionMock).setAutoCommit(eq(false));
	}
	
	@Test
	void runAtomically_consumer_normalCase() throws SQLException {
		Connection connectionMock = mock(Connection.class);
		TransactionSupport testInstance = new TransactionSupport(connectionMock);
		TransactionSupport spy = Mockito.spy(testInstance);
		
		Object[] consumerState = new Object[1];
		spy.runAtomically(c -> { consumerState[0] = c; });
		
		verify(spy).commit();
		verify(connectionMock).commit();
		verify(spy).begin();
		assertThat(consumerState[0]).isSameAs(connectionMock);
		verify(spy).end();
	}
	
	@Test
	void runAtomically_consumer_rollbackOnException() throws SQLException {
		Connection connectionMock = mock(Connection.class);
		TransactionSupport testInstance = new TransactionSupport(connectionMock);
		TransactionSupport spy = Mockito.spy(testInstance);
		
		assertThatThrownBy(() -> spy.runAtomically((ThrowingConsumer<Connection, SQLException>) c -> {
			throw new SQLException();
		})).isNotNull();
		
		verify(spy, never()).commit();
		verify(connectionMock, never()).commit();
		verify(spy).begin();
		verify(spy).rollback();
		verify(spy).end();
	}
	
	@Test
	void runAtomically_function_normalCase() throws SQLException {
		Connection connectionMock = mock(Connection.class);
		TransactionSupport testInstance = new TransactionSupport(connectionMock);
		TransactionSupport spy = Mockito.spy(testInstance);
		
		Connection consumerState = spy.runAtomically((Connection c) -> c);
		
		verify(spy).commit();
		verify(connectionMock).commit();
		verify(spy).begin();
		assertThat(consumerState).isSameAs(connectionMock);
		verify(spy).end();
	}
	
	@Test
	void runAtomically_function_rollbackOnException() throws SQLException {
		Connection connectionMock = mock(Connection.class);
		TransactionSupport testInstance = new TransactionSupport(connectionMock);
		TransactionSupport spy = Mockito.spy(testInstance);
		
		assertThatThrownBy(() -> spy.runAtomically((Connection c) -> {
			throw new SQLException();
		})).isNotNull();
		
		verify(spy, never()).commit();
		verify(connectionMock, never()).commit();
		verify(spy).begin();
		verify(spy).rollback();
		verify(spy).end();
	}
}