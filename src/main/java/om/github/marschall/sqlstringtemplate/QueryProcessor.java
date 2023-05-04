package om.github.marschall.sqlstringtemplate;

import java.lang.StringTemplate;
import java.net.URL;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.sql.DataSource;

public final class QueryProcessor implements StringTemplate.Processor<PreparedExecution, SQLException> {
  
  private final DataSource dataSource;
  
  public QueryProcessor(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  public ResultSet process(StringTemplate st) throws SQLException {
    // Replace StringTemplate placeholders with PreparedStatement placeholders
    String query = String.join("?", st.fragments());


    // delay the execution because we have to close the connection
    return new PreparedExecution(this.dataSource, query, st.values());
}

}
