package com.github.marschall.sqlstringtemplate;

import java.lang.StringTemplate;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Transactional;

import com.github.marschall.sqlstringtemplate.configuration.H2Configuration;

@Transactional
@SpringJUnitConfig(H2Configuration.class)
class JepTests {

  @Autowired
  private DataSource dataSource;

  void jepExample() throws SQLException {
    String name = "ok";
    try (var connection = this.dataSource.getConnection()) {
      StringTemplate.Processor<ResultSet, SQLException> DB = new QueryProcessor(connection);
      try (var resultSet = DB."""
              SELECT \{name}
                FROM dual
              """) {
        assertTrue(resultSet.next());
        assertEquals(name, resultSet.getString(1));
        assertFalse(resultSet.next());
      }
    }
  }

  record QueryProcessor(Connection conn) implements StringTemplate.Processor<ResultSet, SQLException> {

    public ResultSet process(StringTemplate st) throws SQLException {
      // 1. Replace StringTemplate placeholders with PreparedStatement placeholders
      String query = String.join("?", st.fragments());

      // 2. Create the PreparedStatement on the connection
      PreparedStatement preparedStatement = this.conn.prepareStatement(query);

      // 3. Set parameters of the PreparedStatement
      int index = 1;
      for (Object value : st.values()) {
        switch (value) {
          case Integer i -> preparedStatement.setInt(index++, i);
          case Float f   -> preparedStatement.setFloat(index++, f);
          case Double d  -> preparedStatement.setDouble(index++, d);
          case Boolean b -> preparedStatement.setBoolean(index++, b);
          default        -> preparedStatement.setString(index++, String.valueOf(value));
        }
      }

      // 4. Execute the PreparedStatement, returning a ResultSet
      return preparedStatement.executeQuery();
    }
  }

}
