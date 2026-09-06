package com.lifeadmin;

import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        Database.createTable();

        ChoreDAO dao = new ChoreDAO();
        dao.addChore(new Chore(0, "Renew driving licence", LocalDate.of(2026, 10, 15), "License", false));

        for (Chore c : dao.getAllChores()) {
            System.out.println(c);
        }
    }
}