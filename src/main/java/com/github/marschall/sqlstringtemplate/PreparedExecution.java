package com.github.marschall.sqlstringtemplate;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import javax.sql.DataSource;

public final class PreparedExecution {

  private final DataSource dataSource;

  private final String query;

  private final List<Object> parameters;

  PreparedExecution(DataSource dataSource, String query, List<Object> parameters) {
    this.dataSource = dataSource;
    this.query = query;
    this.parameters = parameters;
  }

  public <T> List<T> queryForList(RowMapper<T> rowMapper) throws SQLException {
    return doWithPreparedStatement(preparedStatement -> {
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        List<T> result = new ArrayList<>();
        while (resultSet.next()) {
          result.add(rowMapper.mapRow(resultSet));
        }
        return result;
      }
    });
  }

  public <T> Optional<T> queryForOptional(RowMapper<T> rowMapper) throws SQLException {
    return doWithPreparedStatement(preparedStatement -> {
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        if (resultSet.next()) {
          // here a null check could also be avoided
          Optional<T> result = Optional.ofNullable(rowMapper.mapRow(resultSet));
          if (resultSet.next()) {
            throw new IllegalStateException("more than one object returned");
          }
          return result;
        } else {
          return Optional.empty();
        }
      }
    });
  }

  public <T> T queryForObject(RowMapper<T> rowMapper) throws SQLException {
    return doWithPreparedStatement(preparedStatement -> {
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        if (resultSet.next()) {
          // here a null check could also be avoided
          T result = rowMapper.mapRow(resultSet);
          if (resultSet.next()) {
            throw new IllegalStateException("more than one object returned");
          }
          return result;
        } else {
          throw new NoSuchElementException("no row returned");
        }
      }
    });
  }

  public int update() throws SQLException {
    return doWithPreparedStatement(PreparedStatement::executeUpdate);
  }

  public long laregeUpdate() throws SQLException {
    return doWithPreparedStatement(PreparedStatement::executeLargeUpdate);
  }

  private <T> T doWithPreparedStatement(PreparedStatementCallback<T> callback) throws SQLException {
    try (var connection = this.dataSource.getConnection();
         var preparedStatement = connection.prepareStatement(this.query)) {
     this.setParameters(preparedStatement);
     return callback.doWithPreparedStatement(preparedStatement);
   }
  }

  @FunctionalInterface
  interface PreparedStatementCallback<T> {

    T doWithPreparedStatement(PreparedStatement preparedStatement) throws SQLException;

  }

  private void setParameters(PreparedStatement preparedStatement) throws SQLException {
    int index = 1;
    for (Object parameter : this.parameters) {
      switch (parameter) {
        case Ref r         -> preparedStatement.setRef(index++, r);

        case Date d        -> preparedStatement.setDate(index++, d);
        case Time t        -> preparedStatement.setTime(index++, t);
        case Timestamp ts  -> preparedStatement.setTimestamp(index++, ts);

        case InputStream b -> preparedStatement.setBinaryStream(index++, b);
        case Reader r      -> preparedStatement.setCharacterStream(index++, r);
        case byte[] b      -> preparedStatement.setBytes(index++, b);

        case Blob b        -> preparedStatement.setBlob(index++, b);
        case Array a       -> preparedStatement.setArray(index++, a);
        case NClob nc      -> preparedStatement.setNClob(index++, nc);
        case Clob c        -> preparedStatement.setClob(index++, c);
        case SQLXML s      -> preparedStatement.setSQLXML(index++, s);

        case Byte b        -> preparedStatement.setByte(index++, b);
        case Short s       -> preparedStatement.setShort(index++, s);
        case Integer i     -> preparedStatement.setInt(index++, i);
        case Long l        -> preparedStatement.setLong(index++, l);
        case BigDecimal bd -> preparedStatement.setBigDecimal(index++, bd);
        case Float f       -> preparedStatement.setFloat(index++, f);
        case Double d      -> preparedStatement.setDouble(index++, d);
        case Boolean b     -> preparedStatement.setBoolean(index++, b);
        case URL u         -> preparedStatement.setURL(index++, u);
        default            -> preparedStatement.setObject(index++, parameter);
      }
    }
  }

}
