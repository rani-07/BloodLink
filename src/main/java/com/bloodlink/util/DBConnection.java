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

    public static Connection getConnection() throws SQLException {
        String url  = System.getenv("DB_URL");
        String user = System.getenv("DB_USER");
        String pass = System.getenv("DB_PASS");

        if (url == null || user == null || pass == null) {
            throw new SQLException("Missing DB environment variables (DB_URL/DB_USER/DB_PASS). " +
                    "url=" + url + " user=" + user);
        }

        if (driverInitError != null) {
            throw new SQLException("Driver init failed: " + driverInitError);
        }

        String urlDebug = url.replace("\n", "\\n").replace("\r", "\\r").replace(" ", "\\s");

        try {
            return DriverManager.getConnection(url, user, pass);
        } catch (SQLException e) {
            StringJoiner drivers = new StringJoiner(", ");
            Enumeration<Driver> en = DriverManager.getDrivers();
            while (en.hasMoreElements()) {
                drivers.add(en.nextElement().getClass().getName());
            }
            throw new SQLException("Connect failed. urlLength=" + url.length() +
                    " urlDebug=[" + urlDebug + "] driverRegistered=" + driverRegistered +
                    " registeredDrivers=[" + drivers + "] original=" + e.getMessage(), e);
        }
    }
}
