package com.github.marschall.sqlstringtemplate;

import static org.junit.jupiter.api.Assertions.fail;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Transactional;

import com.github.marschall.sqlstringtemplate.configuration.H2Configuration;

@Transactional
@SpringJUnitConfig(H2Configuration.class)
class QueryProcessorTests {

  @Autowired
  private DataSource dataSource;

  private QueryProcessor queryProcessor;

  @BeforeEach
  void setUp() {
    this.queryProcessor = new QueryProcessor(this.dataSource);
  }

  @Test
  void simpleQuery() {

    String name = "ok";
    var SQL = new PreparedStatementCreatorTemplateProcessor();
    List<String> names = this.jdbcTemplate.query(queryProcessor."""
            SELECT \{name}
            FROM dual
          """.query((rs, i) -> rs.getString(1)));
    assertEquals(List.of(name), names);
  }

}
