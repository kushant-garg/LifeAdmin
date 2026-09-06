package com.lifeadmin;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ChoreDAO {

    public void addChore(Chore chore) {
        String sql = "INSERT INTO chores(title, due_date, category, is_done) VALUES(?,?,?,?)";
        try (Connection conn = DBConnection.connect();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, chore.getTitle());
            ps.setString(2, chore.getDueDate().toString());
            ps.setString(3, chore.getCategory());
            ps.setInt(4, chore.isDone() ? 1 : 0);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Chore> getAllChores() {
        List<Chore> chores = new ArrayList<>();
        String sql = "SELECT * FROM chores";
        try (Connection conn = DBConnection.connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                chores.add(new Chore(
                        rs.getInt("id"),
                        rs.getString("title"),
                        LocalDate.parse(rs.getString("due_date")),
                        rs.getString("category"),
                        rs.getInt("is_done") == 1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return chores;
    }

    public void markDone(int id) {
        String sql = "UPDATE chores SET is_done = 1 WHERE id = ?";
        try (Connection conn = DBConnection.connect();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteChore(int id) {
        String sql = "DELETE FROM chores WHERE id = ?";
        try (Connection conn = DBConnection.connect();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Chore> getChoresDueSoon(int daysAhead) {
        List<Chore> chores = new ArrayList<>();
        String sql = "SELECT * FROM chores WHERE is_done = 0 AND due_date <= ?";
        try (Connection conn = DBConnection.connect();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            LocalDate threshold = LocalDate.now().plusDays(daysAhead);
            ps.setString(1, threshold.toString());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                chores.add(new Chore(
                        rs.getInt("id"), rs.getString("title"),
                        LocalDate.parse(rs.getString("due_date")),
                        rs.getString("category"), rs.getInt("is_done") == 1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return chores;
    }
}