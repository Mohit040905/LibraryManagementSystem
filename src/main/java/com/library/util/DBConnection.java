package com.library.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Singleton utility class to manage the database connection.
 * Reads credentials from db.properties so they are never hardcoded.
 */
public class DBConnection {

    private static Connection connection = null;

    private DBConnection() {}

    public static Connection getConnection() {
        if (connection == null) {
            try {
                Properties props = new Properties();
                InputStream input = DBConnection.class
                        .getClassLoader()
                        .getResourceAsStream("db.properties");

                if (input == null) {
                    throw new RuntimeException("db.properties file not found in resources.");
                }

                props.load(input);

                String url      = props.getProperty("db.url");
                String username = props.getProperty("db.username");
                String password = props.getProperty("db.password");
                String driver   = props.getProperty("db.driver");

                Class.forName(driver);
                connection = DriverManager.getConnection(url, username, password);
                System.out.println("[DB] Connected to MySQL successfully.\n");

            } catch (IOException | ClassNotFoundException | SQLException e) {
                System.err.println("[DB ERROR] Could not connect: " + e.getMessage());
                System.err.println("Check your db.properties file and make sure MySQL is running.");
                System.exit(1);
            }
        }
        return connection;
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("[DB] Connection closed.");
            } catch (SQLException e) {
                System.err.println("[DB ERROR] Failed to close connection: " + e.getMessage());
            }
        }
    }
}
