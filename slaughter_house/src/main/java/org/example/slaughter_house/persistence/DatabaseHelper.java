package org.example.slaughter_house.persistence;

import java.sql.*;

import org.postgresql.Driver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

//jdbc:postgresql://localhost:5432/postgres
@Component public class DatabaseHelper {

  @Value("${spring.datasource.database.url}")
  private String jdbcURL;

  @Value("${spring.datasource.username}")
  private String username;

  @Value("${spring.datasource.password}")
  private String password;

  public DatabaseHelper() throws SQLException {
    DriverManager.registerDriver(new Driver());
  }

  protected Connection getConnection() throws SQLException {
    /*String jdbcURL = "jdbc:postgresql://localhost:5432/postgres";
    String username = "postgres";
    String password = "1706";*/

    if (username == null) {
      return DriverManager.getConnection(jdbcURL);
    }
    else {
      return DriverManager.getConnection(jdbcURL, username, password);
    }
  }

  private PreparedStatement prepare(Connection connection, String sql,
      Object[] parameters) throws SQLException {
    PreparedStatement stat = connection.prepareStatement(sql);
    for (int i = 0; i < parameters.length; i++) {
      stat.setObject(i + 1, parameters[i]);
    }
    return stat;
  }

  public int executeUpdate(String sql, Object... parameters)
      throws SQLException {
    try (Connection connection = getConnection()) {
      PreparedStatement stat = prepare(connection, sql, parameters);
      return stat.executeUpdate();
    }
  }

  public ResultSet executeQuery(String sql, Object... parameters)
      throws SQLException {
    try (Connection connection = getConnection()) {
      PreparedStatement stat = prepare(connection, sql, parameters);
      return stat.executeQuery(sql);
    }
  }
}
