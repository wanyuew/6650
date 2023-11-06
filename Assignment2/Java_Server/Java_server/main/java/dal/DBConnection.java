package main.java.dal;

import java.sql.Connection;
import java.sql.SQLException;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class DBConnection {

    private static HikariDataSource dataSource;

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL JDBC driver not found", e);
        }
        HikariConfig configuration = new HikariConfig();
        configuration.setJdbcUrl("jdbc:mysql://rds-mysql-6650.cudicip8npqc.us-west-2.rds.amazonaws.com:3306/MyDB1?useSSL=false");
        configuration.setUsername("wanyuew");
        configuration.setPassword("wwy12345");
        configuration.setMaximumPoolSize(60);

        dataSource = new HikariDataSource(configuration);
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}

