package com.github.marschall.sqlstringtemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static com.github.marschall.sqlstringtemplate.RowMapper.ofString;

import javax.sql.DataSource;

import java.sql.SQLException;
import java.util.List;

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

  private QueryProcessor database;

  @BeforeEach
  void setUp() {
    this.database = new QueryProcessor(this.dataSource);
  }

  @Test
  void simpleQuery() throws SQLException {

    String name = "ok";
    List<String> names = this.database."""
            SELECT \{name}
            FROM dual
          """.queryForList(ofString());
    assertEquals(List.of(name), names);
  }

}
