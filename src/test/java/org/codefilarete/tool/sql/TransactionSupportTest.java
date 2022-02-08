package org.codefilarete.tool.sql;

import java.sql.Connection;
import java.sql.SQLException;

import org.codefilarete.tool.trace.ModifiableBoolean;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

/**
 * @author Guillaume Mary
 */
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
		assertThat(commitState.getValue()).isFalse();
		
		// end() must set auto-commit back to true
		testInstance.end();
		assertThat(commitState.getValue()).isTrue();
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
		assertThat(commitState.getValue()).isFalse();
		
		// end() must set auto-commit back to true
		testInstance.end();
		assertThat(commitState.getValue()).isFalse();
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
		TransactionSupport spy = Mockito.spy(testInstance);
		
		Object[] consumerState = new Object[1];
		spy.runAtomically(c -> consumerState[0] = c);
		
		verify(spy).commit();
		verify(connectionMock).commit();
		verify(spy).begin();
		assertThat(consumerState[0]).isSameAs(connectionMock);
		verify(spy).end();
	}
	
	@Test
	public void testRunAtomically_rollbackOnException() throws SQLException {
		Connection connectionMock = mock(Connection.class);
		TransactionSupport testInstance = new TransactionSupport(connectionMock);
		TransactionSupport spy = Mockito.spy(testInstance);
		
		assertThatThrownBy(() -> spy.runAtomically(c -> {
			throw new SQLException();
		})).isNotNull();
		
		verify(spy, never()).commit();
		verify(connectionMock, never()).commit();
		verify(spy).begin();
		verify(spy).rollback();
		verify(spy).end();
	}
}