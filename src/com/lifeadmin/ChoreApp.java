package com.lifeadmin;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.util.List;

public class ChoreApp extends Application {
    private ChoreDAO dao = new ChoreDAO();
    private ListView<String> listView = new ListView<>();

    @Override
    public void start(Stage stage) {
        TextField titleField = new TextField();
        titleField.setPromptText("Chore title");

        DatePicker datePicker = new DatePicker();

        Button addButton = new Button("Add Chore");
        addButton.setOnAction(e -> {
            String title = titleField.getText();
            LocalDate date = datePicker.getValue();
            if (title != null && !title.isEmpty() && date != null) {
                dao.addChore(new Chore(0, title, date, "General", false));
                titleField.clear();
                datePicker.setValue(null);
                refreshList();
            }
        });

        HBox form = new HBox(10, titleField, datePicker, addButton);
        form.setPadding(new Insets(10));

        VBox root = new VBox(10, form, listView);
        root.setPadding(new Insets(10));

        refreshList();

        stage.setScene(new Scene(root, 500, 400));
        stage.setTitle("LifeAdmin");
        stage.show();
    }

    private void refreshList() {
        listView.getItems().clear();
        List<Chore> chores = dao.getAllChores();
        for (Chore c : chores) {
            listView.getItems().add(c.toString());
        }
    }

    public static void main(String[] args) {
        Database.createTable();
        launch(args);
    }
}