package com.example.utils;

import com.example.ui.MainController;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    public static Connection getConnection() throws SQLException, SecurityException{

        String url = "jdbc:sqlite:" + MainController.getCurrentDbFile().getAbsolutePath();
        return DriverManager.getConnection(url);
    }
}
