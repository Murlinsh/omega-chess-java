package main.gui;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import main.*;
import main.pieces.*;

public class PromotionDialog {
    private Stage stage;
    private Game game;
    private Position pawnPosition;
    private ChessBoardView boardView;

    public PromotionDialog(Game game, Position pawnPosition, ChessBoardView boardView) {
        this.game = game;
        this.pawnPosition = pawnPosition;
        this.boardView = boardView;
        createDialog();
    }

    private void createDialog() {
        stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.UTILITY);
        stage.setTitle("Превращение пешки");

        VBox root = new VBox(20);
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #f8f8f8;");

        Text title = new Text("Выберите фигуру для превращения:");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        HBox buttonsBox = new HBox(15);
        buttonsBox.setAlignment(Pos.CENTER);

        // Создаем кнопки для каждой фигуры
        String[] pieceNames = {"Ферзь", "Ладья", "Слон", "Конь"};
        Class<? extends Piece>[] pieceClasses = new Class[]{
                Queen.class, Rook.class, Bishop.class, Knight.class
        };

        for (int i = 0; i < pieceNames.length; i++) {
            int index = i;
            Button button = new Button(pieceNames[i]);
            button.setPrefSize(80, 40);
            button.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

            // Устанавливаем символ фигуры
            switch (pieceNames[i]) {
                case "Ферзь": button.setText("♕"); break;
                case "Ладья": button.setText("♖"); break;
                case "Слон": button.setText("♗"); break;
                case "Конь": button.setText("♘"); break;
            }

            button.setOnAction(e -> {
                game.promotePawn(pawnPosition, pieceClasses[index]);
                boardView.drawBoard();
                stage.close();
            });

            buttonsBox.getChildren().add(button);
        }

        root.getChildren().addAll(title, buttonsBox);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setResizable(false);
    }

    public void show() {
        stage.showAndWait();
    }
}