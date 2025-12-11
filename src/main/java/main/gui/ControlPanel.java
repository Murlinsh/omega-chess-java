package main.gui;

import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import main.GameType;

public class ControlPanel {
    private VBox view;
    private ChessApp app;
    private TextArea logArea;

    public ControlPanel(ChessApp app) {
        this.app = app;
        createView();
    }

    private void createView() {
        view = new VBox(15);
        view.setPadding(new Insets(20));
        view.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #ccc; -fx-border-width: 0 0 0 2;");
        view.setPrefWidth(250);

        Label title = new Label("Управление");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Кнопка новой игры
        Button newGameBtn = new Button("Новая игра");
        newGameBtn.setPrefWidth(200);
        newGameBtn.setOnAction(e -> {
            app.newGame(GameType.CLASSIC);
            log("Новая игра начата");
        });

        // Кнопка отмены хода
        Button undoBtn = new Button("Отменить ход");
        undoBtn.setPrefWidth(200);
        undoBtn.setOnAction(e -> {
            app.undoMove();
            log("Попытка отменить ход");
        });

        // Кнопка сдачи
        Button surrenderBtn = new Button("Сдаться");
        surrenderBtn.setPrefWidth(200);
        surrenderBtn.setStyle("-fx-background-color: #ff4444; -fx-text-fill: white;");
        surrenderBtn.setOnAction(e -> {
            app.surrender();
            log("Игрок сдался");
        });

        // Кнопка выхода
        Button exitBtn = new Button("Выход");
        exitBtn.setPrefWidth(200);
        exitBtn.setOnAction(e -> System.exit(0));

        // Лог событий
        Label logLabel = new Label("Лог игры:");
        logLabel.setStyle("-fx-font-weight: bold;");

        logArea = new TextArea();
        logArea.setPrefHeight(200);
        logArea.setEditable(false);
        logArea.setWrapText(true);
        log("Игра начата");

        view.getChildren().addAll(
                title, newGameBtn, undoBtn, surrenderBtn, exitBtn,
                logLabel, logArea
        );

        view.setAlignment(Pos.TOP_CENTER);
    }

    public void log(String message) {
        if (logArea != null) {
            logArea.appendText(message + "\n");
        }
        System.out.println("LOG: " + message);
    }

    public VBox getView() {
        return view;
    }
}