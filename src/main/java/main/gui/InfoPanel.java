package main.gui;

import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import main.Game;
import main.Color; // наш Color enum

public class InfoPanel {
    private VBox view;
    private Game game;

    private Label currentPlayerLabel;
    private Label gameStatusLabel;
    private Label capturedWhiteLabel;
    private Label capturedBlackLabel;
    private Label checkLabel;

    public InfoPanel(Game game) {
        this.game = game;
        createView();
    }

    private void createView() {
        view = new VBox(10);
        view.setPadding(new Insets(15));
        view.setStyle("-fx-background-color: #2c3e50; -fx-text-fill: white;");
        view.setPrefHeight(100);

        // Заголовок
        Label title = new Label("Информация о игре");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: white;");

        // Панель с информацией
        HBox infoBox = new HBox(30);
        infoBox.setAlignment(Pos.CENTER);

        // Текущий игрок
        VBox playerBox = new VBox(5);
        Label playerTitle = new Label("Текущий ход:");
        playerTitle.setStyle("-fx-text-fill: #ecf0f1;");
        currentPlayerLabel = new Label("Белые");
        currentPlayerLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: white;");
        playerBox.getChildren().addAll(playerTitle, currentPlayerLabel);

        // Статус игры
        VBox statusBox = new VBox(5);
        Label statusTitle = new Label("Статус игры:");
        statusTitle.setStyle("-fx-text-fill: #ecf0f1;");
        gameStatusLabel = new Label("Игра идет");
        gameStatusLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2ecc71;");
        statusBox.getChildren().addAll(statusTitle, gameStatusLabel);

        // Съеденные фигуры (белые)
        VBox capturedWhiteBox = new VBox(5);
        Label capturedWhiteTitle = new Label("Съедено белых:");
        capturedWhiteTitle.setStyle("-fx-text-fill: #ecf0f1;");
        capturedWhiteLabel = new Label("0 фигур");
        capturedWhiteLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: white;");
        capturedWhiteBox.getChildren().addAll(capturedWhiteTitle, capturedWhiteLabel);

        // Съеденные фигуры (черные)
        VBox capturedBlackBox = new VBox(5);
        Label capturedBlackTitle = new Label("Съедено черных:");
        capturedBlackTitle.setStyle("-fx-text-fill: #ecf0f1;");
        capturedBlackLabel = new Label("0 фигур");
        capturedBlackLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: white;");
        capturedBlackBox.getChildren().addAll(capturedBlackTitle, capturedBlackLabel);

        // Шах
        VBox checkBox = new VBox(5);
        Label checkTitle = new Label("Шах:");
        checkTitle.setStyle("-fx-text-fill: #ecf0f1;");
        checkLabel = new Label("Нет");
        checkLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #e74c3c;");
        checkBox.getChildren().addAll(checkTitle, checkLabel);

        infoBox.getChildren().addAll(playerBox, statusBox, capturedWhiteBox, capturedBlackBox, checkBox);

        view.getChildren().addAll(title, infoBox);
    }

    public void updateInfo() {
        if (game == null) return;

        // Текущий игрок - используем полное имя для javafx.scene.paint.Color
        currentPlayerLabel.setText(game.getCurrentPlayer() == Color.WHITE ? "Белые" : "Черные");

        // Для цвета текста используем javafx.scene.paint.Color
        javafx.scene.paint.Color textColor = game.getCurrentPlayer() == Color.WHITE
                ? javafx.scene.paint.Color.WHITE
                : javafx.scene.paint.Color.BLACK;
        currentPlayerLabel.setTextFill(textColor);

        // Статус игры
        if (game.isGameOver()) {
            gameStatusLabel.setText("Игра завершена");
            gameStatusLabel.setTextFill(javafx.scene.paint.Color.RED);
        } else {
            gameStatusLabel.setText("Игра идет");
            gameStatusLabel.setTextFill(javafx.scene.paint.Color.GREEN);
        }

        // Съеденные фигуры
        capturedWhiteLabel.setText(game.getCapturedWhitePieces().size() + " фигур");
        capturedBlackLabel.setText(game.getCapturedBlackPieces().size() + " фигур");

        // Шах
        if (game.getBoard().isKingInCheck(game.getCurrentPlayer())) {
            checkLabel.setText("ДА");
            checkLabel.setTextFill(javafx.scene.paint.Color.RED);
        } else {
            checkLabel.setText("Нет");
            checkLabel.setTextFill(javafx.scene.paint.Color.WHITE);
        }
    }

    public void updateGame(Game newGame) {
        this.game = newGame;
        updateInfo();
    }

    public VBox getView() {
        return view;
    }
}