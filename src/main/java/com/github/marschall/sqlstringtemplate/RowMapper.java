package com.github.marschall.sqlstringtemplate;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

@FunctionalInterface
public interface RowMapper<T> {

  T mapRow(ResultSet resultSet) throws SQLException;

  public static RowMapper<String> string() {
    return resultSet -> resultSet.getString(1);
  }

  public static RowMapper<Integer> integer() {
    return resultSet -> resultSet.getInt(1);
  }
  public static RowMapper<BigDecimal> bigDecimal() {
    return resultSet -> resultSet.getBigDecimal(1);
  }

  public static <T> RowMapper<T> object(Class<T> clazz) {
    return resultSet -> resultSet.getObject(1, clazz);
  }

}
