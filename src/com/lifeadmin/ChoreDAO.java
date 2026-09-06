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
                    rs.getInt("is_done") == 1
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return chores;
    }
}