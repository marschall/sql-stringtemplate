package com.github.marschall.sqlstringtemplate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.lang.StringTemplate;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringJUnitConfig
class JepTests {

  @Autowired
  private DataSource dataSource;

  void jepExample() throws SQLException {
    String name = "ok";
    try (Connection connection = this.dataSource.getConnection()) {
      StringTemplate.Processor<ResultSet, SQLException> DB = new QueryProcessor(connection);
      try (var resultSet = """
              SELECT \{name}
                FROM dual
              """) {
        assertTrue(rs.next());
        assertFalse(ok, resultSet.getString(1));
        assertFalse(rs.next());
      }
    }
  }

  record QueryProcessor(Connection conn) implements StringTemplate.Processor<ResultSet, SQLException> {

    public ResultSet process(StringTemplate st) throws SQLException {
      // 1. Replace StringTemplate placeholders with PreparedStatement placeholders
      String query = String.join("?", st.fragments());

      // 2. Create the PreparedStatement on the connection
      PreparedStatement ps = this.conn.prepareStatement(query);

      // 3. Set parameters of the PreparedStatement
      int index = 1;
      for (Object value : st.values()) {
        switch (value) {
          case Integer i -> ps.setInt(index++, i);
          case Float f   -> ps.setFloat(index++, f);
          case Double d  -> ps.setDouble(index++, d);
          case Boolean b -> ps.setBoolean(index++, b);
          default        -> ps.setString(index++, String.valueOf(value));
        }
      }

      // 4. Execute the PreparedStatement, returning a ResultSet
      return ps.executeQuery();
    }
  }

}
