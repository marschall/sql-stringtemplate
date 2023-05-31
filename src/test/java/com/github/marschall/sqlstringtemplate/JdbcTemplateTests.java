package com.github.marschall.sqlstringtemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.SQLException;
import java.util.List;

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
import org.springframework.jdbc.core.RowMapper;

import com.github.marschall.sqlstringtemplate.configuration.H2Configuration;
import org.springframework.dao.DataAccessException;

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
    List<String> names = this.jdbcTemplate.query(SQL."""
            SELECT \{name}
            FROM dual
          """, (rs, i) -> rs.getString(1));
    assertEquals(List.of(name), names);
  }

  static final class PreparedStatementCreatorTemplateProcessor implements StringTemplate.Processor<PreparedStatementCreator, DataAccessException> {

    public PreparedStatementCreator process(StringTemplate st) {
      String sql = String.join("?", st.fragments());
      return new PreparedStatementCreatorFactory(sql).newPreparedStatementCreator(st.values());
    }
  }

}
