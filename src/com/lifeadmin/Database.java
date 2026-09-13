package com.lifeadmin;

import java.sql.*;

public class Database {
    public static void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS chores (" +
                "id INT PRIMARY KEY AUTO_INCREMENT," +
                "title VARCHAR(255) NOT NULL," +
                "due_date VARCHAR(20) NOT NULL," +
                "category VARCHAR(100)," +
                "is_done INT DEFAULT 0," +
                "document_path VARCHAR(500)," +
                "user_id INT NOT NULL," +
                "recurrence VARCHAR(20) DEFAULT 'none'" +
                ")";
        try (Connection conn = DBConnection.connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Chores table ready.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}