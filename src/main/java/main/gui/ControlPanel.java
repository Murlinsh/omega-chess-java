package main.gui;

import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import main.GameType;
import main.Game;
import javafx.scene.control.Separator;

public class ControlPanel {
    private VBox view;
    private ChessApp app;
    private TextArea logArea;
    private Button newClassicBtn;
    private Button newOmegaBtn;

    public ControlPanel(ChessApp app) {
        this.app = app;
        createView();
    }

    private void createView() {
        view = new VBox(15);
        view.setPadding(new Insets(20));
        view.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #ccc; -fx-border-width: 0 0 0 2;");
        view.setPrefWidth(280);

        Label title = new Label("Управление");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Раздел "Тип игры"
        Label gameTypeLabel = new Label("Новая игра:");
        gameTypeLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        // Кнопка новой классической игры
        newClassicBtn = new Button("Классика (8×8)");
        newClassicBtn.setPrefWidth(240);
        newClassicBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
        newClassicBtn.setOnAction(e -> {
            app.newGame(GameType.CLASSIC);
            updateButtonStyles(GameType.CLASSIC);
            log("Новая классическая игра начата");
        });

        // Кнопка новой Omega игры
        newOmegaBtn = new Button("Omega Chess (10×10)");
        newOmegaBtn.setPrefWidth(240);
        newOmegaBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        newOmegaBtn.setOnAction(e -> {
            app.newGame(GameType.OMEGA);
            updateButtonStyles(GameType.OMEGA);
            log("Новая Omega Chess игра начата");
        });

        Separator separator1 = new Separator();

        // Кнопка отмены хода
        Button undoBtn = new Button("Отменить ход (Ctrl+Z)");
        undoBtn.setPrefWidth(240);
        undoBtn.setOnAction(e -> {
            app.undoMove();
            log("Попытка отменить ход");
        });

        // Кнопка сдачи
        Button surrenderBtn = new Button("Сдаться (Esc)");
        surrenderBtn.setPrefWidth(240);
        surrenderBtn.setStyle("-fx-background-color: #ff4444; -fx-text-fill: white;");
        surrenderBtn.setOnAction(e -> {
            app.surrender();
            log("Игрок сдался");
        });

        Separator separator2 = new Separator();

        // Кнопка выхода
        Button exitBtn = new Button("Выход");
        exitBtn.setPrefWidth(240);
        exitBtn.setOnAction(e -> System.exit(0));

        // Лог событий
        Label logLabel = new Label("Лог игры:");
        logLabel.setStyle("-fx-font-weight: bold;");

        logArea = new TextArea();
        logArea.setPrefHeight(250);
        logArea.setEditable(false);
        logArea.setWrapText(true);
        logArea.setStyle("-fx-font-family: 'Monospaced'; -fx-font-size: 12px;");
        log("Игра начата. Выберите тип игры.");

        // Добавляем горячие клавиши
        setupKeyboardShortcuts();

        view.getChildren().addAll(
                title, gameTypeLabel, newClassicBtn, newOmegaBtn,
                separator1, undoBtn, surrenderBtn, separator2,
                exitBtn, logLabel, logArea
        );

        view.setAlignment(Pos.TOP_CENTER);

        // Устанавливаем начальный стиль кнопок (по умолчанию CLASSIC)
        updateButtonStyles(GameType.CLASSIC);
    }

    private void updateButtonStyles(GameType currentType) {
        if (currentType == GameType.CLASSIC) {
            newClassicBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-border-color: #2E7D32; -fx-border-width: 2;");
            newOmegaBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-weight: normal; -fx-border-width: 0;");
        } else {
            newClassicBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: normal; -fx-border-width: 0;");
            newOmegaBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-weight: bold; -fx-border-color: #0D47A1; -fx-border-width: 2;");
        }
    }

    private void setupKeyboardShortcuts() {
        view.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case Z:
                    if (event.isControlDown()) {
                        app.undoMove();
                        log("Горячая клавиша: отмена хода (Ctrl+Z)");
                    }
                    break;
                case F2:
                    app.newGame(GameType.CLASSIC);
                    log("Горячая клавиша: новая игра (F2)");
                    break;
                case F3:
                    app.newGame(GameType.OMEGA);
                    log("Горячая клавиша: новая Omega игра (F3)");
                    break;
                case ESCAPE:
                    app.surrender();
                    log("Горячая клавиша: сдача (Esc)");
                    break;
            }
        });
    }

    public void log(String message) {
        if (logArea != null) {
            logArea.appendText("> " + message + "\n");
            // Автопрокрутка вниз
            logArea.setScrollTop(Double.MAX_VALUE);
        }
        System.out.println("LOG: " + message);
    }

    public VBox getView() {
        return view;
    }

    public void updateGameType(GameType gameType) {
        updateButtonStyles(gameType);
    }
}