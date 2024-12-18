package com.librarymanagement;

import java.sql.Connection;
import java.sql.DriverManager;

public class DbConnection {
    private static Connection connection;

    public static Connection getConnection() {
        if (connection == null) {
            System.out.println("Connection Called");
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/librarymanagement", "root", "2103");
                System.out.println("Created Connection");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return connection;
    }


}
