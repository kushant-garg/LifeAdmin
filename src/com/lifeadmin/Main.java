package com.lifeadmin;

import java.time.LocalDate;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Database.createTable();

        ChoreDAO dao = new ChoreDAO();
        System.out.println("\nChores due in next 30 days:");

List<Chore> dueSoon = dao.getChoresDueSoon(30);

for (Chore chore : dueSoon) {
    System.out.println(chore);
}
        dao.addChore(new Chore(0, "Renew driving licence", LocalDate.of(2026, 10, 15), "License", false));

        for (Chore c : dao.getAllChores()) {
            System.out.println(c);
        }
    }
}