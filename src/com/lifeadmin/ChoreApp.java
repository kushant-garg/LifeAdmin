package com.lifeadmin;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.List;

public class ChoreApp extends Application {
    private ChoreDAO dao = new ChoreDAO();
    private UserDAO userDAO = new UserDAO();
    private DocumentDAO documentDAO = new DocumentDAO();
    private VBox choreListBox = new VBox(8);
    private VBox documentListBox = new VBox(8);
    private BorderPane mainLayout = new BorderPane();
    private Stage primaryStage;
    private User currentUser;

    private static final String APP_STORAGE_DIR = System.getProperty("user.home") + File.separator + "LifeAdminDocuments";

    private static final String BG = "-fx-background-color: #f0fdf9;";
    private static final String CARD = "-fx-background-color: white; -fx-background-radius: 12; -fx-effect: dropshadow(gaussian, rgba(13,148,136,0.12), 12, 0, 0, 3);";
    private static final String CARD_DUE_SOON = "-fx-background-color: white; -fx-background-radius: 12; -fx-border-color: #f59e0b; -fx-border-width: 0 0 0 4; -fx-border-radius: 12; -fx-effect: dropshadow(gaussian, rgba(245,158,11,0.2), 12, 0, 0, 3);";
    private static final String PRIMARY_BTN = "-fx-background-color: #0d9488; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 9 18; -fx-cursor: hand;";
    private static final String SECONDARY_BTN = "-fx-background-color: #ccfbf1; -fx-text-fill: #0f766e; -fx-background-radius: 8; -fx-padding: 9 18; -fx-cursor: hand;";
    private static final String DONE_BTN = "-fx-background-color: #16a34a; -fx-text-fill: white; -fx-background-radius: 8; -fx-padding: 7 15; -fx-cursor: hand;";
    private static final String DELETE_BTN = "-fx-background-color: #fef2f2; -fx-text-fill: #dc2626; -fx-background-radius: 8; -fx-padding: 7 15; -fx-cursor: hand;";
    private static final String NAV_BTN = "-fx-background-color: transparent; -fx-text-fill: #99f6e4; -fx-font-size: 14; -fx-padding: 10 18; -fx-cursor: hand;";
    private static final String NAV_BTN_ACTIVE = "-fx-background-color: #0d9488; -fx-text-fill: white; -fx-font-size: 14; -fx-background-radius: 8; -fx-padding: 10 18; -fx-cursor: hand;";

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        new File(APP_STORAGE_DIR).mkdirs();
        Database.createTable();
        userDAO.createUserTable();
        documentDAO.createTable();
        showLoginScreen(stage);
    }

    private void showLoginScreen(Stage stage) {
        Label heading = new Label("LifeAdmin");
        heading.setFont(Font.font("Segoe UI", FontWeight.BOLD, 32));
        heading.setTextFill(Color.web("#0d9488"));

        Label subheading = new Label("Track your deadlines. Never miss a renewal.");
        subheading.setStyle("-fx-text-fill: #6b7280;");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setMaxWidth(280);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setMaxWidth(280);

        Button loginButton = new Button("Login");
        loginButton.setStyle(PRIMARY_BTN);
        Button registerButton = new Button("Register");
        registerButton.setStyle(SECONDARY_BTN);

        Label statusLabel = new Label();

        loginButton.setOnAction(e -> {
            User user = userDAO.login(usernameField.getText(), passwordField.getText());
            if (user != null) {
                currentUser = user;
                showDashboard(stage);
            } else {
                statusLabel.setStyle("-fx-text-fill: #dc2626;");
                statusLabel.setText("Invalid username or password.");
            }
        });

        registerButton.setOnAction(e -> {
            boolean success = userDAO.register(usernameField.getText(), passwordField.getText());
            statusLabel.setStyle(success ? "-fx-text-fill: #10b981;" : "-fx-text-fill: #dc2626;");
            statusLabel.setText(success ? "Registered! Now click Login." : "Username already taken.");
        });

        HBox buttonRow = new HBox(10, loginButton, registerButton);
        buttonRow.setAlignment(Pos.CENTER);

        VBox loginCard = new VBox(14, heading, subheading, usernameField, passwordField, buttonRow, statusLabel);
        loginCard.setPadding(new Insets(40));
        loginCard.setAlignment(Pos.CENTER);
        loginCard.setMaxWidth(360);
        loginCard.setStyle(CARD);

        StackPane root = new StackPane(loginCard);
        root.setStyle(BG);
        stage.setScene(new Scene(root));
        stage.setMaximized(true);
        stage.setTitle("LifeAdmin - Login");
        stage.show();
    }

    private void showDashboard(Stage stage) {
        Button choresTab = new Button("Chores");
        Button documentsTab = new Button("Documents");
        choresTab.setStyle(NAV_BTN_ACTIVE);
        documentsTab.setStyle(NAV_BTN);

        Label welcome = new Label("Welcome, " + currentUser.getUsername());
        welcome.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        welcome.setStyle("-fx-text-fill: white;");

        HBox nav = new HBox(10, choresTab, documentsTab, spacer(), welcome);
        nav.setAlignment(Pos.CENTER_LEFT);
        nav.setPadding(new Insets(14, 24, 14, 24));
        nav.setStyle("-fx-background-color: #042f2e;");

        mainLayout.setTop(nav);
        mainLayout.setStyle(BG);

        choresTab.setOnAction(e -> {
            choresTab.setStyle(NAV_BTN_ACTIVE);
            documentsTab.setStyle(NAV_BTN);
            mainLayout.setCenter(buildChoresView());
        });

        documentsTab.setOnAction(e -> {
            documentsTab.setStyle(NAV_BTN_ACTIVE);
            choresTab.setStyle(NAV_BTN);
            mainLayout.setCenter(buildDocumentsView());
        });

        mainLayout.setCenter(buildChoresView());

        stage.setScene(new Scene(mainLayout));
        stage.setMaximized(true);
        stage.setTitle("LifeAdmin");
    }

    private Region spacer() {
        Region r = new Region();
        HBox.setHgrow(r, Priority.ALWAYS);
        return r;
    }

    private ScrollPane buildChoresView() {
        TextField titleField = new TextField();
        titleField.setPromptText("Chore title (e.g. Renew driving licence)");
        titleField.setPrefWidth(240);

        DatePicker datePicker = new DatePicker();
        datePicker.setPromptText("Due date");

        ComboBox<String> recurrenceBox = new ComboBox<>();
        recurrenceBox.getItems().addAll("none", "weekly", "monthly", "yearly");
        recurrenceBox.setValue("none");

        Button addButton = new Button("+ Add Chore");
        addButton.setStyle(PRIMARY_BTN);
        addButton.setOnAction(e -> {
            String title = titleField.getText();
            LocalDate date = datePicker.getValue();
            String recurrence = recurrenceBox.getValue();
            if (title != null && !title.isEmpty() && date != null) {
                dao.addChore(new Chore(0, title, date, "General", false, currentUser.getId(), recurrence), null);
                titleField.clear();
                datePicker.setValue(null);
                recurrenceBox.setValue("none");
                refreshChoreList();
            }
        });

        HBox form = new HBox(10, titleField, datePicker, recurrenceBox, addButton);
        form.setAlignment(Pos.CENTER_LEFT);
        form.setPadding(new Insets(16));
        form.setStyle(CARD);

        Label listHeading = new Label("Your Chores");
        listHeading.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));

        refreshChoreList();

        VBox content = new VBox(16, form, listHeading, choreListBox);
        content.setPadding(new Insets(24));

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        return scroll;
    }

    private void refreshChoreList() {
        choreListBox.getChildren().clear();
        List<Chore> chores = dao.getAllChores(currentUser.getId());
        List<Chore> dueSoon = dao.getChoresDueSoon(currentUser.getId(), 7);

        if (chores.isEmpty()) {
            Label empty = new Label("No chores yet — add one above.");
            empty.setStyle("-fx-text-fill: #9ca3af;");
            choreListBox.getChildren().add(empty);
            return;
        }
        for (Chore c : chores) {
            boolean isDueSoon = dueSoon.stream().anyMatch(d -> d.getId() == c.getId());

            Label title = new Label(c.getTitle());
            title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));

            String recurrenceTag = c.getRecurrence() != null && !c.getRecurrence().equals("none")
                    ? "  •  repeats " + c.getRecurrence() : "";
            Label details = new Label("Due " + c.getDueDate() + "  •  " + c.getCategory()
                    + recurrenceTag + (isDueSoon ? "  •  Due soon" : ""));
            details.setStyle(isDueSoon ? "-fx-text-fill: #f59e0b; -fx-font-weight: bold;" : "-fx-text-fill: #6b7280;");

            VBox textBox = new VBox(2, title, details);
            HBox.setHgrow(textBox, Priority.ALWAYS);

            Button doneButton = new Button(c.isDone() ? "✓ Done" : "Mark Done");
            doneButton.setStyle(c.isDone() ? DONE_BTN : PRIMARY_BTN);
            doneButton.setDisable(c.isDone());
            doneButton.setOnAction(e -> {
                dao.completeChore(c.getId());
                refreshChoreList();
            });

            Button deleteButton = new Button("Delete");
            deleteButton.setStyle(DELETE_BTN);
            deleteButton.setOnAction(e -> {
                dao.deleteChore(c.getId());
                refreshChoreList();
            });

            HBox row = new HBox(16, textBox, doneButton, deleteButton);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(14));
            row.setStyle((isDueSoon && !c.isDone()) ? CARD_DUE_SOON : CARD);
            choreListBox.getChildren().add(row);
        }
    }

    private ScrollPane buildDocumentsView() {
        Label titleFieldLabel = new Label("Document title:");
        TextField titleField = new TextField();
        titleField.setPromptText("e.g. Driving Licence");
        titleField.setPrefWidth(220);

        Button chooseFileButton = new Button("Choose File");
        chooseFileButton.setStyle(SECONDARY_BTN);
        Label fileLabel = new Label("No file selected");
        fileLabel.setStyle("-fx-text-fill: #6b7280;");

        File[] selected = new File[1];

        chooseFileButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select a document to upload");
            File file = fileChooser.showOpenDialog(primaryStage);
            if (file != null) {
                selected[0] = file;
                fileLabel.setText(file.getName());
            }
        });

        Button uploadButton = new Button("Upload");
        uploadButton.setStyle(PRIMARY_BTN);
        uploadButton.setOnAction(e -> {
            if (selected[0] != null && !titleField.getText().isEmpty()) {
                try {
                    String newFileName = System.currentTimeMillis() + "_" + selected[0].getName();
                    Path destination = Paths.get(APP_STORAGE_DIR, newFileName);
                    Files.copy(selected[0].toPath(), destination, StandardCopyOption.REPLACE_EXISTING);
                    documentDAO.addDocument(titleField.getText(), destination.toString(), currentUser.getId());
                    titleField.clear();
                    fileLabel.setText("No file selected");
                    selected[0] = null;
                    refreshDocumentList();
                } catch (IOException ex) {
                    fileLabel.setText("Upload failed: " + ex.getMessage());
                }
            }
        });

        HBox form = new HBox(10, titleFieldLabel, titleField, chooseFileButton, fileLabel, uploadButton);
        form.setAlignment(Pos.CENTER_LEFT);
        form.setPadding(new Insets(16));
        form.setStyle(CARD);

        Label listHeading = new Label("Your Documents");
        listHeading.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));

        refreshDocumentList();

        VBox content = new VBox(16, form, listHeading, documentListBox);
        content.setPadding(new Insets(24));

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        return scroll;
    }

    private void refreshDocumentList() {
        documentListBox.getChildren().clear();
        List<Document> docs = documentDAO.getAllDocuments(currentUser.getId());
        if (docs.isEmpty()) {
            Label empty = new Label("No documents uploaded yet.");
            empty.setStyle("-fx-text-fill: #9ca3af;");
            documentListBox.getChildren().add(empty);
            return;
        }
        for (Document d : docs) {
            Label title = new Label(d.getTitle());
            title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));

            Label details = new Label("Uploaded " + d.getUploadDate());
            details.setStyle("-fx-text-fill: #6b7280;");

            VBox textBox = new VBox(2, title, details);
            HBox.setHgrow(textBox, Priority.ALWAYS);

            Button openButton = new Button("Open");
            openButton.setStyle(SECONDARY_BTN);
            openButton.setOnAction(e -> {
                try {
                    java.awt.Desktop.getDesktop().open(new File(d.getFilePath()));
                } catch (IOException ex) {
                    title.setText(title.getText() + " (couldn't open file)");
                }
            });

            Button deleteButton = new Button("Delete");
            deleteButton.setStyle(DELETE_BTN);
            deleteButton.setOnAction(e -> {
                documentDAO.deleteDocument(d.getId());
                new File(d.getFilePath()).delete();
                refreshDocumentList();
            });

            HBox row = new HBox(16, textBox, openButton, deleteButton);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(14));
            row.setStyle(CARD);
            documentListBox.getChildren().add(row);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}