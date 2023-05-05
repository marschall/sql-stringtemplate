package com.github.marschall.sqlstringtemplate;

import static org.junit.jupiter.api.Assertions.fail;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Transactional;

import com.github.marschall.sqlstringtemplate.configuration.H2Configuration;

@Transactional
@SpringJUnitConfig(H2Configuration.class)
class JdbcTemplateTests {

  @Autowired
  private DataSource dataSource;

  private JdbcOperations jdbcTemplate;

  @BeforeEach
  void setUp() {
    this.jdbcTemplate = new JdbcTemplate(this.dataSource);
  }

  @Test
  void query() {
    String name = "ok";
    var SQL = new PreparedStatementCreatorTemplateProcessor();
    List<Strin> names = this.jdbcTemplate.query(SQL."""
            SELECT \{name}
            FROM dual
          """, (RowMapper) (rs, i) -> rs.getString(1));
    assertEquals(List.of(name), names);
  }

  static final class PreparedStatementCreatorTemplateProcessor implements StringTemplate.Processor<PreparedStatementCreator, SQLException> {

    public PreparedStatementCreator process(StringTemplate st) throws SQLException {
      String sql = String.join("?", st.fragments());
      return new PreparedStatementCreatorFactory(sql).newPreparedStatementCreator(st.values());
    }
  }

}
