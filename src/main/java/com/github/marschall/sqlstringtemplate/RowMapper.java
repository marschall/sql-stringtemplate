package com.github.marschall.sqlstringtemplate;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

@FunctionalInterface
public interface RowMapper<T> {

  T mapRow(ResultSet resultSet) throws SQLException;

  public static RowMapper<String> ofString() {
    return resultSet -> resultSet.getString(1);
  }

  public static RowMapper<Integer> ofInteger() {
    return resultSet -> resultSet.getInt(1);
  }

  public static RowMapper<Long> ofLong() {
    return resultSet -> resultSet.getLong(1);
  }

  public static RowMapper<BigDecimal> ofBigDecimal() {
    return resultSet -> resultSet.getBigDecimal(1);
  }

  public static <T> RowMapper<T> ofObject(Class<T> clazz) {
    return resultSet -> resultSet.getObject(1, clazz);
  }

}
