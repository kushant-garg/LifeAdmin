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
    private VBox choreListBox = new VBox(5);

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

        ScrollPane scrollPane = new ScrollPane(choreListBox);
        scrollPane.setFitToWidth(true);

        VBox root = new VBox(10, form, scrollPane);
        root.setPadding(new Insets(10));

        refreshList();

        stage.setScene(new Scene(root, 550, 450));
        stage.setTitle("LifeAdmin");
        stage.show();
    }

    private void refreshList() {
        choreListBox.getChildren().clear();
        List<Chore> chores = dao.getAllChores();
        for (Chore c : chores) {
            Label label = new Label(c.toString());
            label.setPrefWidth(380);

            Button doneButton = new Button(c.isDone() ? "✓ Done" : "Mark Done");
            doneButton.setDisable(c.isDone());
            doneButton.setOnAction(e -> {
                dao.markDone(c.getId());
                refreshList();
            });

            HBox row = new HBox(10, label, doneButton);
            choreListBox.getChildren().add(row);
        }
    }

    public static void main(String[] args) {
        Database.createTable();
        launch(args);
    }
}