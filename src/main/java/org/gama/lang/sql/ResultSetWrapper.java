package org.gama.lang.sql;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLType;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;

/**
 * {@link ResultSet} that wraps another one and delegates all its methods to it without any additionnal feature.
 * Made to override only some targeted methods.
 *
 * @author Guillaume Mary
 */
public class ResultSetWrapper implements ResultSet {
	
	protected final ResultSet surrogate;
	
	public ResultSetWrapper(ResultSet surrogate) {
		this.surrogate = surrogate;
	}
	
	public boolean next() throws SQLException {
		return surrogate.next();
	}
	
	public void close() throws SQLException {
		surrogate.close();
	}
	
	public boolean wasNull() throws SQLException {
		return surrogate.wasNull();
	}
	
	public String getString(int columnIndex) throws SQLException {
		return surrogate.getString(columnIndex);
	}
	
	public boolean getBoolean(int columnIndex) throws SQLException {
		return surrogate.getBoolean(columnIndex);
	}
	
	public byte getByte(int columnIndex) throws SQLException {
		return surrogate.getByte(columnIndex);
	}
	
	public short getShort(int columnIndex) throws SQLException {
		return surrogate.getShort(columnIndex);
	}
	
	public int getInt(int columnIndex) throws SQLException {
		return surrogate.getInt(columnIndex);
	}
	
	public long getLong(int columnIndex) throws SQLException {
		return surrogate.getLong(columnIndex);
	}
	
	public float getFloat(int columnIndex) throws SQLException {
		return surrogate.getFloat(columnIndex);
	}
	
	public double getDouble(int columnIndex) throws SQLException {
		return surrogate.getDouble(columnIndex);
	}
	
	@Deprecated
	public BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
		return surrogate.getBigDecimal(columnIndex, scale);
	}
	
	public byte[] getBytes(int columnIndex) throws SQLException {
		return surrogate.getBytes(columnIndex);
	}
	
	public Date getDate(int columnIndex) throws SQLException {
		return surrogate.getDate(columnIndex);
	}
	
	public Time getTime(int columnIndex) throws SQLException {
		return surrogate.getTime(columnIndex);
	}
	
	public Timestamp getTimestamp(int columnIndex) throws SQLException {
		return surrogate.getTimestamp(columnIndex);
	}
	
	public InputStream getAsciiStream(int columnIndex) throws SQLException {
		return surrogate.getAsciiStream(columnIndex);
	}
	
	@Deprecated
	public InputStream getUnicodeStream(int columnIndex) throws SQLException {
		return surrogate.getUnicodeStream(columnIndex);
	}
	
	public InputStream getBinaryStream(int columnIndex) throws SQLException {
		return surrogate.getBinaryStream(columnIndex);
	}
	
	public String getString(String columnLabel) throws SQLException {
		return surrogate.getString(columnLabel);
	}
	
	public boolean getBoolean(String columnLabel) throws SQLException {
		return surrogate.getBoolean(columnLabel);
	}
	
	public byte getByte(String columnLabel) throws SQLException {
		return surrogate.getByte(columnLabel);
	}
	
	public short getShort(String columnLabel) throws SQLException {
		return surrogate.getShort(columnLabel);
	}
	
	public int getInt(String columnLabel) throws SQLException {
		return surrogate.getInt(columnLabel);
	}
	
	public long getLong(String columnLabel) throws SQLException {
		return surrogate.getLong(columnLabel);
	}
	
	public float getFloat(String columnLabel) throws SQLException {
		return surrogate.getFloat(columnLabel);
	}
	
	public double getDouble(String columnLabel) throws SQLException {
		return surrogate.getDouble(columnLabel);
	}
	
	@Deprecated
	public BigDecimal getBigDecimal(String columnLabel, int scale) throws SQLException {
		return surrogate.getBigDecimal(columnLabel, scale);
	}
	
	public byte[] getBytes(String columnLabel) throws SQLException {
		return surrogate.getBytes(columnLabel);
	}
	
	public Date getDate(String columnLabel) throws SQLException {
		return surrogate.getDate(columnLabel);
	}
	
	public Time getTime(String columnLabel) throws SQLException {
		return surrogate.getTime(columnLabel);
	}
	
	public Timestamp getTimestamp(String columnLabel) throws SQLException {
		return surrogate.getTimestamp(columnLabel);
	}
	
	public InputStream getAsciiStream(String columnLabel) throws SQLException {
		return surrogate.getAsciiStream(columnLabel);
	}
	
	@Deprecated
	public InputStream getUnicodeStream(String columnLabel) throws SQLException {
		return surrogate.getUnicodeStream(columnLabel);
	}
	
	public InputStream getBinaryStream(String columnLabel) throws SQLException {
		return surrogate.getBinaryStream(columnLabel);
	}
	
	public SQLWarning getWarnings() throws SQLException {
		return surrogate.getWarnings();
	}
	
	public void clearWarnings() throws SQLException {
		surrogate.clearWarnings();
	}
	
	public String getCursorName() throws SQLException {
		return surrogate.getCursorName();
	}
	
	public ResultSetMetaData getMetaData() throws SQLException {
		return surrogate.getMetaData();
	}
	
	public Object getObject(int columnIndex) throws SQLException {
		return surrogate.getObject(columnIndex);
	}
	
	public Object getObject(String columnLabel) throws SQLException {
		return surrogate.getObject(columnLabel);
	}
	
	public int findColumn(String columnLabel) throws SQLException {
		return surrogate.findColumn(columnLabel);
	}
	
	public Reader getCharacterStream(int columnIndex) throws SQLException {
		return surrogate.getCharacterStream(columnIndex);
	}
	
	public Reader getCharacterStream(String columnLabel) throws SQLException {
		return surrogate.getCharacterStream(columnLabel);
	}
	
	public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
		return surrogate.getBigDecimal(columnIndex);
	}
	
	public BigDecimal getBigDecimal(String columnLabel) throws SQLException {
		return surrogate.getBigDecimal(columnLabel);
	}
	
	public boolean isBeforeFirst() throws SQLException {
		return surrogate.isBeforeFirst();
	}
	
	public boolean isAfterLast() throws SQLException {
		return surrogate.isAfterLast();
	}
	
	public boolean isFirst() throws SQLException {
		return surrogate.isFirst();
	}
	
	public boolean isLast() throws SQLException {
		return surrogate.isLast();
	}
	
	public void beforeFirst() throws SQLException {
		surrogate.beforeFirst();
	}
	
	public void afterLast() throws SQLException {
		surrogate.afterLast();
	}
	
	public boolean first() throws SQLException {
		return surrogate.first();
	}
	
	public boolean last() throws SQLException {
		return surrogate.last();
	}
	
	public int getRow() throws SQLException {
		return surrogate.getRow();
	}
	
	public boolean absolute(int row) throws SQLException {
		return surrogate.absolute(row);
	}
	
	public boolean relative(int rows) throws SQLException {
		return surrogate.relative(rows);
	}
	
	public boolean previous() throws SQLException {
		return surrogate.previous();
	}
	
	public int getFetchDirection() throws SQLException {
		return surrogate.getFetchDirection();
	}
	
	public void setFetchDirection(int direction) throws SQLException {
		surrogate.setFetchDirection(direction);
	}
	
	public int getFetchSize() throws SQLException {
		return surrogate.getFetchSize();
	}
	
	public void setFetchSize(int rows) throws SQLException {
		surrogate.setFetchSize(rows);
	}
	
	public int getType() throws SQLException {
		return surrogate.getType();
	}
	
	public int getConcurrency() throws SQLException {
		return surrogate.getConcurrency();
	}
	
	public boolean rowUpdated() throws SQLException {
		return surrogate.rowUpdated();
	}
	
	public boolean rowInserted() throws SQLException {
		return surrogate.rowInserted();
	}
	
	public boolean rowDeleted() throws SQLException {
		return surrogate.rowDeleted();
	}
	
	public void updateNull(int columnIndex) throws SQLException {
		surrogate.updateNull(columnIndex);
	}
	
	public void updateBoolean(int columnIndex, boolean x) throws SQLException {
		surrogate.updateBoolean(columnIndex, x);
	}
	
	public void updateByte(int columnIndex, byte x) throws SQLException {
		surrogate.updateByte(columnIndex, x);
	}
	
	public void updateShort(int columnIndex, short x) throws SQLException {
		surrogate.updateShort(columnIndex, x);
	}
	
	public void updateInt(int columnIndex, int x) throws SQLException {
		surrogate.updateInt(columnIndex, x);
	}
	
	public void updateLong(int columnIndex, long x) throws SQLException {
		surrogate.updateLong(columnIndex, x);
	}
	
	public void updateFloat(int columnIndex, float x) throws SQLException {
		surrogate.updateFloat(columnIndex, x);
	}
	
	public void updateDouble(int columnIndex, double x) throws SQLException {
		surrogate.updateDouble(columnIndex, x);
	}
	
	public void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException {
		surrogate.updateBigDecimal(columnIndex, x);
	}
	
	public void updateString(int columnIndex, String x) throws SQLException {
		surrogate.updateString(columnIndex, x);
	}
	
	public void updateBytes(int columnIndex, byte[] x) throws SQLException {
		surrogate.updateBytes(columnIndex, x);
	}
	
	public void updateDate(int columnIndex, Date x) throws SQLException {
		surrogate.updateDate(columnIndex, x);
	}
	
	public void updateTime(int columnIndex, Time x) throws SQLException {
		surrogate.updateTime(columnIndex, x);
	}
	
	public void updateTimestamp(int columnIndex, Timestamp x) throws SQLException {
		surrogate.updateTimestamp(columnIndex, x);
	}
	
	public void updateAsciiStream(int columnIndex, InputStream x, int length) throws SQLException {
		surrogate.updateAsciiStream(columnIndex, x, length);
	}
	
	public void updateBinaryStream(int columnIndex, InputStream x, int length) throws SQLException {
		surrogate.updateBinaryStream(columnIndex, x, length);
	}
	
	public void updateCharacterStream(int columnIndex, Reader x, int length) throws SQLException {
		surrogate.updateCharacterStream(columnIndex, x, length);
	}
	
	public void updateObject(int columnIndex, Object x, int scaleOrLength) throws SQLException {
		surrogate.updateObject(columnIndex, x, scaleOrLength);
	}
	
	public void updateObject(int columnIndex, Object x) throws SQLException {
		surrogate.updateObject(columnIndex, x);
	}
	
	public void updateNull(String columnLabel) throws SQLException {
		surrogate.updateNull(columnLabel);
	}
	
	public void updateBoolean(String columnLabel, boolean x) throws SQLException {
		surrogate.updateBoolean(columnLabel, x);
	}
	
	public void updateByte(String columnLabel, byte x) throws SQLException {
		surrogate.updateByte(columnLabel, x);
	}
	
	public void updateShort(String columnLabel, short x) throws SQLException {
		surrogate.updateShort(columnLabel, x);
	}
	
	public void updateInt(String columnLabel, int x) throws SQLException {
		surrogate.updateInt(columnLabel, x);
	}
	
	public void updateLong(String columnLabel, long x) throws SQLException {
		surrogate.updateLong(columnLabel, x);
	}
	
	public void updateFloat(String columnLabel, float x) throws SQLException {
		surrogate.updateFloat(columnLabel, x);
	}
	
	public void updateDouble(String columnLabel, double x) throws SQLException {
		surrogate.updateDouble(columnLabel, x);
	}
	
	public void updateBigDecimal(String columnLabel, BigDecimal x) throws SQLException {
		surrogate.updateBigDecimal(columnLabel, x);
	}
	
	public void updateString(String columnLabel, String x) throws SQLException {
		surrogate.updateString(columnLabel, x);
	}
	
	public void updateBytes(String columnLabel, byte[] x) throws SQLException {
		surrogate.updateBytes(columnLabel, x);
	}
	
	public void updateDate(String columnLabel, Date x) throws SQLException {
		surrogate.updateDate(columnLabel, x);
	}
	
	public void updateTime(String columnLabel, Time x) throws SQLException {
		surrogate.updateTime(columnLabel, x);
	}
	
	public void updateTimestamp(String columnLabel, Timestamp x) throws SQLException {
		surrogate.updateTimestamp(columnLabel, x);
	}
	
	public void updateAsciiStream(String columnLabel, InputStream x, int length) throws SQLException {
		surrogate.updateAsciiStream(columnLabel, x, length);
	}
	
	public void updateBinaryStream(String columnLabel, InputStream x, int length) throws SQLException {
		surrogate.updateBinaryStream(columnLabel, x, length);
	}
	
	public void updateCharacterStream(String columnLabel, Reader reader, int length) throws SQLException {
		surrogate.updateCharacterStream(columnLabel, reader, length);
	}
	
	public void updateObject(String columnLabel, Object x, int scaleOrLength) throws SQLException {
		surrogate.updateObject(columnLabel, x, scaleOrLength);
	}
	
	public void updateObject(String columnLabel, Object x) throws SQLException {
		surrogate.updateObject(columnLabel, x);
	}
	
	public void insertRow() throws SQLException {
		surrogate.insertRow();
	}
	
	public void updateRow() throws SQLException {
		surrogate.updateRow();
	}
	
	public void deleteRow() throws SQLException {
		surrogate.deleteRow();
	}
	
	public void refreshRow() throws SQLException {
		surrogate.refreshRow();
	}
	
	public void cancelRowUpdates() throws SQLException {
		surrogate.cancelRowUpdates();
	}
	
	public void moveToInsertRow() throws SQLException {
		surrogate.moveToInsertRow();
	}
	
	public void moveToCurrentRow() throws SQLException {
		surrogate.moveToCurrentRow();
	}
	
	public Statement getStatement() throws SQLException {
		return surrogate.getStatement();
	}
	
	public Object getObject(int columnIndex, Map<String, Class<?>> map) throws SQLException {
		return surrogate.getObject(columnIndex, map);
	}
	
	public Ref getRef(int columnIndex) throws SQLException {
		return surrogate.getRef(columnIndex);
	}
	
	public Blob getBlob(int columnIndex) throws SQLException {
		return surrogate.getBlob(columnIndex);
	}
	
	public Clob getClob(int columnIndex) throws SQLException {
		return surrogate.getClob(columnIndex);
	}
	
	public Array getArray(int columnIndex) throws SQLException {
		return surrogate.getArray(columnIndex);
	}
	
	public Object getObject(String columnLabel, Map<String, Class<?>> map) throws SQLException {
		return surrogate.getObject(columnLabel, map);
	}
	
	public Ref getRef(String columnLabel) throws SQLException {
		return surrogate.getRef(columnLabel);
	}
	
	public Blob getBlob(String columnLabel) throws SQLException {
		return surrogate.getBlob(columnLabel);
	}
	
	public Clob getClob(String columnLabel) throws SQLException {
		return surrogate.getClob(columnLabel);
	}
	
	public Array getArray(String columnLabel) throws SQLException {
		return surrogate.getArray(columnLabel);
	}
	
	public Date getDate(int columnIndex, Calendar cal) throws SQLException {
		return surrogate.getDate(columnIndex, cal);
	}
	
	public Date getDate(String columnLabel, Calendar cal) throws SQLException {
		return surrogate.getDate(columnLabel, cal);
	}
	
	public Time getTime(int columnIndex, Calendar cal) throws SQLException {
		return surrogate.getTime(columnIndex, cal);
	}
	
	public Time getTime(String columnLabel, Calendar cal) throws SQLException {
		return surrogate.getTime(columnLabel, cal);
	}
	
	public Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
		return surrogate.getTimestamp(columnIndex, cal);
	}
	
	public Timestamp getTimestamp(String columnLabel, Calendar cal) throws SQLException {
		return surrogate.getTimestamp(columnLabel, cal);
	}
	
	public URL getURL(int columnIndex) throws SQLException {
		return surrogate.getURL(columnIndex);
	}
	
	public URL getURL(String columnLabel) throws SQLException {
		return surrogate.getURL(columnLabel);
	}
	
	public void updateRef(int columnIndex, Ref x) throws SQLException {
		surrogate.updateRef(columnIndex, x);
	}
	
	public void updateRef(String columnLabel, Ref x) throws SQLException {
		surrogate.updateRef(columnLabel, x);
	}
	
	public void updateBlob(int columnIndex, Blob x) throws SQLException {
		surrogate.updateBlob(columnIndex, x);
	}
	
	public void updateBlob(String columnLabel, Blob x) throws SQLException {
		surrogate.updateBlob(columnLabel, x);
	}
	
	public void updateClob(int columnIndex, Clob x) throws SQLException {
		surrogate.updateClob(columnIndex, x);
	}
	
	public void updateClob(String columnLabel, Clob x) throws SQLException {
		surrogate.updateClob(columnLabel, x);
	}
	
	public void updateArray(int columnIndex, Array x) throws SQLException {
		surrogate.updateArray(columnIndex, x);
	}
	
	public void updateArray(String columnLabel, Array x) throws SQLException {
		surrogate.updateArray(columnLabel, x);
	}
	
	public RowId getRowId(int columnIndex) throws SQLException {
		return surrogate.getRowId(columnIndex);
	}
	
	public RowId getRowId(String columnLabel) throws SQLException {
		return surrogate.getRowId(columnLabel);
	}
	
	public void updateRowId(int columnIndex, RowId x) throws SQLException {
		surrogate.updateRowId(columnIndex, x);
	}
	
	public void updateRowId(String columnLabel, RowId x) throws SQLException {
		surrogate.updateRowId(columnLabel, x);
	}
	
	public int getHoldability() throws SQLException {
		return surrogate.getHoldability();
	}
	
	public boolean isClosed() throws SQLException {
		return surrogate.isClosed();
	}
	
	public void updateNString(int columnIndex, String nString) throws SQLException {
		surrogate.updateNString(columnIndex, nString);
	}
	
	public void updateNString(String columnLabel, String nString) throws SQLException {
		surrogate.updateNString(columnLabel, nString);
	}
	
	public void updateNClob(int columnIndex, NClob nClob) throws SQLException {
		surrogate.updateNClob(columnIndex, nClob);
	}
	
	public void updateNClob(String columnLabel, NClob nClob) throws SQLException {
		surrogate.updateNClob(columnLabel, nClob);
	}
	
	public NClob getNClob(int columnIndex) throws SQLException {
		return surrogate.getNClob(columnIndex);
	}
	
	public NClob getNClob(String columnLabel) throws SQLException {
		return surrogate.getNClob(columnLabel);
	}
	
	public SQLXML getSQLXML(int columnIndex) throws SQLException {
		return surrogate.getSQLXML(columnIndex);
	}
	
	public SQLXML getSQLXML(String columnLabel) throws SQLException {
		return surrogate.getSQLXML(columnLabel);
	}
	
	public void updateSQLXML(int columnIndex, SQLXML xmlObject) throws SQLException {
		surrogate.updateSQLXML(columnIndex, xmlObject);
	}
	
	public void updateSQLXML(String columnLabel, SQLXML xmlObject) throws SQLException {
		surrogate.updateSQLXML(columnLabel, xmlObject);
	}
	
	public String getNString(int columnIndex) throws SQLException {
		return surrogate.getNString(columnIndex);
	}
	
	public String getNString(String columnLabel) throws SQLException {
		return surrogate.getNString(columnLabel);
	}
	
	public Reader getNCharacterStream(int columnIndex) throws SQLException {
		return surrogate.getNCharacterStream(columnIndex);
	}
	
	public Reader getNCharacterStream(String columnLabel) throws SQLException {
		return surrogate.getNCharacterStream(columnLabel);
	}
	
	public void updateNCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
		surrogate.updateNCharacterStream(columnIndex, x, length);
	}
	
	public void updateNCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
		surrogate.updateNCharacterStream(columnLabel, reader, length);
	}
	
	public void updateAsciiStream(int columnIndex, InputStream x, long length) throws SQLException {
		surrogate.updateAsciiStream(columnIndex, x, length);
	}
	
	public void updateBinaryStream(int columnIndex, InputStream x, long length) throws SQLException {
		surrogate.updateBinaryStream(columnIndex, x, length);
	}
	
	public void updateCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
		surrogate.updateCharacterStream(columnIndex, x, length);
	}
	
	public void updateAsciiStream(String columnLabel, InputStream x, long length) throws SQLException {
		surrogate.updateAsciiStream(columnLabel, x, length);
	}
	
	public void updateBinaryStream(String columnLabel, InputStream x, long length) throws SQLException {
		surrogate.updateBinaryStream(columnLabel, x, length);
	}
	
	public void updateCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
		surrogate.updateCharacterStream(columnLabel, reader, length);
	}
	
	public void updateBlob(int columnIndex, InputStream inputStream, long length) throws SQLException {
		surrogate.updateBlob(columnIndex, inputStream, length);
	}
	
	public void updateBlob(String columnLabel, InputStream inputStream, long length) throws SQLException {
		surrogate.updateBlob(columnLabel, inputStream, length);
	}
	
	public void updateClob(int columnIndex, Reader reader, long length) throws SQLException {
		surrogate.updateClob(columnIndex, reader, length);
	}
	
	public void updateClob(String columnLabel, Reader reader, long length) throws SQLException {
		surrogate.updateClob(columnLabel, reader, length);
	}
	
	public void updateNClob(int columnIndex, Reader reader, long length) throws SQLException {
		surrogate.updateNClob(columnIndex, reader, length);
	}
	
	public void updateNClob(String columnLabel, Reader reader, long length) throws SQLException {
		surrogate.updateNClob(columnLabel, reader, length);
	}
	
	public void updateNCharacterStream(int columnIndex, Reader x) throws SQLException {
		surrogate.updateNCharacterStream(columnIndex, x);
	}
	
	public void updateNCharacterStream(String columnLabel, Reader reader) throws SQLException {
		surrogate.updateNCharacterStream(columnLabel, reader);
	}
	
	public void updateAsciiStream(int columnIndex, InputStream x) throws SQLException {
		surrogate.updateAsciiStream(columnIndex, x);
	}
	
	public void updateBinaryStream(int columnIndex, InputStream x) throws SQLException {
		surrogate.updateBinaryStream(columnIndex, x);
	}
	
	public void updateCharacterStream(int columnIndex, Reader x) throws SQLException {
		surrogate.updateCharacterStream(columnIndex, x);
	}
	
	public void updateAsciiStream(String columnLabel, InputStream x) throws SQLException {
		surrogate.updateAsciiStream(columnLabel, x);
	}
	
	public void updateBinaryStream(String columnLabel, InputStream x) throws SQLException {
		surrogate.updateBinaryStream(columnLabel, x);
	}
	
	public void updateCharacterStream(String columnLabel, Reader reader) throws SQLException {
		surrogate.updateCharacterStream(columnLabel, reader);
	}
	
	public void updateBlob(int columnIndex, InputStream inputStream) throws SQLException {
		surrogate.updateBlob(columnIndex, inputStream);
	}
	
	public void updateBlob(String columnLabel, InputStream inputStream) throws SQLException {
		surrogate.updateBlob(columnLabel, inputStream);
	}
	
	public void updateClob(int columnIndex, Reader reader) throws SQLException {
		surrogate.updateClob(columnIndex, reader);
	}
	
	public void updateClob(String columnLabel, Reader reader) throws SQLException {
		surrogate.updateClob(columnLabel, reader);
	}
	
	public void updateNClob(int columnIndex, Reader reader) throws SQLException {
		surrogate.updateNClob(columnIndex, reader);
	}
	
	public void updateNClob(String columnLabel, Reader reader) throws SQLException {
		surrogate.updateNClob(columnLabel, reader);
	}
	
	public <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
		return surrogate.getObject(columnIndex, type);
	}
	
	public <T> T getObject(String columnLabel, Class<T> type) throws SQLException {
		return surrogate.getObject(columnLabel, type);
	}
	
	public void updateObject(int columnIndex, Object x, SQLType targetSqlType, int scaleOrLength) throws SQLException {
		surrogate.updateObject(columnIndex, x, targetSqlType, scaleOrLength);
	}
	
	public void updateObject(String columnLabel, Object x, SQLType targetSqlType, int scaleOrLength) throws SQLException {
		surrogate.updateObject(columnLabel, x, targetSqlType, scaleOrLength);
	}
	
	public void updateObject(int columnIndex, Object x, SQLType targetSqlType) throws SQLException {
		surrogate.updateObject(columnIndex, x, targetSqlType);
	}
	
	public void updateObject(String columnLabel, Object x, SQLType targetSqlType) throws SQLException {
		surrogate.updateObject(columnLabel, x, targetSqlType);
	}
	
	public <T> T unwrap(Class<T> iface) throws SQLException {
		return surrogate.unwrap(iface);
	}
	
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return surrogate.isWrapperFor(iface);
	}
}
