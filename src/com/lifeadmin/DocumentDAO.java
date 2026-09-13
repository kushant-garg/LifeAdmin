package com.lifeadmin;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DocumentDAO {

    public void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS documents (" +
                "id INT PRIMARY KEY AUTO_INCREMENT," +
                "title VARCHAR(255) NOT NULL," +
                "file_path VARCHAR(500) NOT NULL," +
                "upload_date VARCHAR(20) NOT NULL," +
                "user_id INT NOT NULL" +
                ")";
        try (Connection conn = DBConnection.connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addDocument(String title, String filePath, int userId) {
        String sql = "INSERT INTO documents(title, file_path, upload_date, user_id) VALUES(?,?,?,?)";
        try (Connection conn = DBConnection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, title);
            ps.setString(2, filePath);
            ps.setString(3, LocalDate.now().toString());
            ps.setInt(4, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Document> getAllDocuments(int userId) {
        List<Document> docs = new ArrayList<>();
        String sql = "SELECT * FROM documents WHERE user_id = ? ORDER BY upload_date DESC";
        try (Connection conn = DBConnection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                docs.add(new Document(
                    rs.getInt("id"), rs.getString("title"),
                    rs.getString("file_path"), rs.getString("upload_date")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return docs;
    }

    public void deleteDocument(int id) {
        String sql = "DELETE FROM documents WHERE id = ?";
        try (Connection conn = DBConnection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}