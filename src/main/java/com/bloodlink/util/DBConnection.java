package com.bloodlink.util;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    static {
        try {
            // Explicitly instantiate and register the driver instead of relying
            // on Class.forName's static-init side effect, which can fail to
            // re-register after Tomcat forcibly unregisters drivers on redeploy/restart.
            Driver driver = (Driver) Class.forName("com.mysql.cj.jdbc.Driver")
                    .getDeclaredConstructor().newInstance();
            DriverManager.registerDriver(driver);
        } catch (Exception e) {
            throw new RuntimeException("Failed to register MySQL JDBC driver", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        String url  = System.getenv("DB_URL");
        String user = System.getenv("DB_USER");
        String pass = System.getenv("DB_PASS");
        if (url == null || user == null || pass == null) {
            throw new SQLException("Missing DB environment variables (DB_URL/DB_USER/DB_PASS). " +
                    "url=" + url + " user=" + user);
        }
        return DriverManager.getConnection(url, user, pass);
    }
}
