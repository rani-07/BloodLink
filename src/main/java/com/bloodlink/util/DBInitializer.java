package com.bloodlink.util;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@WebListener
public class DBInitializer implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("database.sql")) {
            if (in == null) {
                System.err.println("[DBInitializer] database.sql not found on classpath, skipping.");
                return;
            }

            String sql = readAll(in);
            String[] statements = sql.split(";");

            try (Connection conn = DBConnection.getConnection();
                 Statement stmt = conn.createStatement()) {

                for (String raw : statements) {
                    String s = raw.trim();
                    if (s.isEmpty() || s.startsWith("--")) continue;
                    try {
                        stmt.execute(s);
                    } catch (SQLException e) {
                        String msg = e.getMessage() == null ? "" : e.getMessage().toLowerCase();
                        if (msg.contains("duplicate") || msg.contains("already exists")) {
                            System.out.println("[DBInitializer] Skipping (already applied): " + s.substring(0, Math.min(60, s.length())));
                        } else {
                            System.err.println("[DBInitializer] Statement failed: " + s.substring(0, Math.min(60, s.length())));
                            System.err.println("[DBInitializer] Error: " + e.getMessage());
                        }
                    }
                }
                System.out.println("[DBInitializer] Schema initialization complete.");
            }
        } catch (Exception e) {
            System.err.println("[DBInitializer] Failed to initialize database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }

    private static String readAll(InputStream in) throws Exception {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        }
        return sb.toString();
    }
}
