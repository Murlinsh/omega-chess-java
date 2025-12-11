package main.gui;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import main.Piece;
import main.King;
import main.Queen;
import main.Rook;
import main.Bishop;
import main.Knight;
import main.Pawn;

public class PieceView {
    private Piece piece;
    private StackPane view;

    public PieceView(Piece piece) {
        this.piece = piece;
        createView();
    }

    private void createView() {
        view = new StackPane();
        view.setPrefSize(70, 70);

        // Символы Unicode для шахматных фигур
        String symbol = getPieceSymbol();

        Text pieceText = new Text(symbol);
        pieceText.setFont(Font.font("Segoe UI Symbol", FontWeight.BOLD, 40));

        // Улучшенные цвета для лучшего контраста
        if (piece.getColor() == main.Color.WHITE) {
            pieceText.setFill(Color.WHITE);
            // Мощная тень для белых фигур на темных клетках
            javafx.scene.effect.DropShadow shadow = new javafx.scene.effect.DropShadow();
            shadow.setColor(Color.rgb(0, 0, 0, 0.9)); // Почти черная тень
            shadow.setRadius(4);
            shadow.setSpread(0.3);
            pieceText.setEffect(shadow);
        } else {
            // Для черных фигур используем темный, но не чисто черный цвет
            pieceText.setFill(Color.rgb(44, 62, 80)); // Темно-синий/серый
            // Светлая тень для черных фигур на светлых клетках
            javafx.scene.effect.DropShadow shadow = new javafx.scene.effect.DropShadow();
            shadow.setColor(Color.rgb(255, 255, 255, 0.7)); // Белая тень
            shadow.setRadius(4);
            shadow.setSpread(0.3);
            pieceText.setEffect(shadow);
        }

        view.getChildren().add(pieceText);
    }

    private String getPieceSymbol() {
        if (piece instanceof King) return "♔";
        else if (piece instanceof Queen) return "♕";
        else if (piece instanceof Rook) return "♖";
        else if (piece instanceof Bishop) return "♗";
        else if (piece instanceof Knight) return "♘";
        else if (piece instanceof Pawn) return "♙";
        else return "?";
    }

    public StackPane getView() {
        return view;
    }

    public Piece getPiece() {
        return piece;
    }
}