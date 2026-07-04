package com.bloodlink.util;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.StringJoiner;

public class DBConnection {

    private static volatile boolean driverRegistered = false;
    private static volatile String driverInitError = null;

    static {
        try {
            Driver driver = (Driver) Class.forName("com.mysql.cj.jdbc.Driver")
                    .getDeclaredConstructor().newInstance();
            DriverManager.registerDriver(driver);
            driverRegistered = true;
        } catch (Throwable e) {
            driverInitError = e.getClass().getName() + ": " + e.getMessage();
        }
    }

    private static String trim(String s) {
        return s == null ? null : s.trim();
    }

    public static Connection getConnection() throws SQLException {
        String url  = trim(System.getenv("DB_URL"));
        String user = trim(System.getenv("DB_USER"));
        String pass = trim(System.getenv("DB_PASS"));

        if (url == null || user == null || pass == null) {
            throw new SQLException("Missing DB environment variables (DB_URL/DB_USER/DB_PASS). " +
                    "url=" + url + " user=" + user);
        }

        if (driverInitError != null) {
            throw new SQLException("Driver init failed: " + driverInitError);
        }

        try {
            return DriverManager.getConnection(url, user, pass);
        } catch (SQLException e) {
            throw new SQLException("Connect failed: " + e.getMessage(), e);
        }
    }
}
