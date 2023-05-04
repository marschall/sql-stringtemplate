package om.github.marschall.sqlstringtemplate;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

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
  
  int update() throws SQLException {
    try (var connection = this.dataSource.getConnection();
         var preparedStatement = connection.prepareStatement(query)) {
      setParameters(preparedStatement);
      return preparedStatement.executeUpdate();
    }
  }
  
  long laregeUpdate() throws SQLException {
    try (var connection = this.dataSource.getConnection();
        var preparedStatement = connection.prepareStatement(query)) {
      setParameters(preparedStatement);
      return preparedStatement.executeLargeUpdate();
    }
  }
  
  private void setParameters(PreparedStatement preparedStatement) throws SQLException {
    int index = 1;
    for (Object parameter : this.parameters) {
        switch (parameter) {
            case Integer i -> preparedStatement.setInt(index++, i);
            case Long l    -> preparedStatement.setLong(index++, l);
            case BigDecimal bd -> preparedStatement.setBigDecimal(index++, bd);
            case Float f   -> preparedStatement.setFloat(index++, f);
            case Double d  -> preparedStatement.setDouble(index++, d);
            case Boolean b -> preparedStatement.setBoolean(index++, b);
            case URL u     -> preparedStatement.setURL(index++, u);
            default        -> preparedStatement.setObject(index++, parameter);
        }
    }
  }

}
