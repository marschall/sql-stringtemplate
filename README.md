

Why
---

People may copy and paste the sample code from the JEP. The sample code of the JEP has the following issues:

- It requires operating on a `Connection`. In general `DataSource` offers easier framework integration and does not require a try-with-resources block.
- The `PreparedStatement` is never closed. In theory the connection pool should track all open objects and close the `ResultSet`. However not all connection pools do this directly.
- It puts the burden on the caller to close the `ResultSet`.
- It binds only a limited number of objects, relying on the string representation for others rather than relying on the driver.

This results in code like this:

```java
try (var connection = this.dataSource.getConnection()) {
  StringTemplate.Processor<ResultSet, SQLException> DB = new QueryProcessor(connection);
  try (var resultSet = DB."SELECT * FROM Person p WHERE p.last_name = \{name}") {
    while (resultSet.next()) {
      // processing
    }
  }
}
```

Limitations
-----------

- SQL strings are not cached resulting in a lot of unnecessary allocations. SQL strings can get quite large quite quickly.
