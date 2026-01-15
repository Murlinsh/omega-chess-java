package main.gui;

import javafx.scene.layout.StackPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.effect.DropShadow;
import main.pieces.Piece;
import main.pieces.King;
import main.pieces.Queen;
import main.pieces.Rook;
import main.pieces.Bishop;
import main.pieces.Knight;
import main.pieces.Pawn;
import main.pieces.Champion;
import main.pieces.Wizard;

import java.io.InputStream;

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

        // Загружаем изображение
        Image image = loadPieceImage();

        if (image != null && !image.isError()) {
            ImageView pieceImage = new ImageView(image);
            pieceImage.setFitWidth(56);
            pieceImage.setFitHeight(56);
            pieceImage.setPreserveRatio(true);
            pieceImage.setSmooth(true);

            DropShadow shadow = new DropShadow();
            shadow.setColor(Color.rgb(0, 0, 0, 0.4));
            shadow.setRadius(4);
            shadow.setOffsetX(1);
            shadow.setOffsetY(1);
            pieceImage.setEffect(shadow);

            view.getChildren().add(pieceImage);

            System.out.println("Изображение загружено: " + getPieceName() + " " +
                    (piece.getColor() == main.Color.WHITE ? "белая" : "черная"));
        } else {
            System.out.println("Использую fallback для: " + getPieceName());
            createFallbackView();
        }
    }

    private Image loadPieceImage() {
        String imagePath = getPieceImagePath();

        try {
            InputStream is = getClass().getResourceAsStream(imagePath);

            if (is == null) {
                System.err.println("Ресурс не найден: " + imagePath);

                // Пробуем другие варианты
                String[] alternatives = {
                        imagePath,
                        imagePath.substring(1),
                        "resources" + imagePath,
                        "/resources" + imagePath,
                        "../resources" + imagePath
                };

                for (String altPath : alternatives) {
                    System.out.println("Пробую альтернативный путь: " + altPath);
                    is = getClass().getResourceAsStream(altPath);
                    if (is != null) {
                        System.out.println("Найден по пути: " + altPath);
                        break;
                    }
                }
            }

            if (is == null) {
                return null;
            }

            Image image = new Image(is);
            is.close();
            return image;

        } catch (Exception e) {
            System.err.println("Ошибка загрузки изображения: " + e.getMessage());
            return null;
        }
    }

    private String getPieceImagePath() {
        String color = piece.getColor() == main.Color.WHITE ? "white" : "black";
        String type = "";

        if (piece instanceof King) type = "king";
        else if (piece instanceof Queen) type = "queen";
        else if (piece instanceof Rook) type = "rook";
        else if (piece instanceof Bishop) type = "bishop";
        else if (piece instanceof Knight) type = "knight";
        else if (piece instanceof Pawn) type = "pawn";
        else if (piece instanceof Champion) type = "champion";
        else if (piece instanceof Wizard) type = "wizard";

        // Путь к изображению
        return "/pieces/" + color + "_" + type + ".png";
    }

    private void createFallbackView() {
        // Текстовые символы как запасной вариант
        String symbol = getPieceSymbol();
        javafx.scene.text.Text pieceText = new javafx.scene.text.Text(symbol);
        pieceText.setFont(javafx.scene.text.Font.font("Arial", javafx.scene.text.FontWeight.BOLD, 36));

        if (piece.getColor() == main.Color.WHITE) {
            pieceText.setFill(Color.WHITE);
            DropShadow shadow = new DropShadow();
            shadow.setColor(Color.rgb(0, 0, 0, 0.7));
            shadow.setRadius(3);
            pieceText.setEffect(shadow);
        } else {
            pieceText.setFill(Color.rgb(30, 30, 30));
            DropShadow shadow = new DropShadow();
            shadow.setColor(Color.rgb(255, 255, 255, 0.7));
            shadow.setRadius(3);
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
        else if (piece instanceof Champion) return "C";
        else if (piece instanceof Wizard) return "W";
        else return "?";
    }

    private String getPieceName() {
        if (piece instanceof King) return "король";
        else if (piece instanceof Queen) return "ферзь";
        else if (piece instanceof Rook) return "ладья";
        else if (piece instanceof Bishop) return "слон";
        else if (piece instanceof Knight) return "конь";
        else if (piece instanceof Pawn) return "пешка";
        else if (piece instanceof Champion) return "чемпион";
        else if (piece instanceof Wizard) return "волшебник";
        else return "фигура";
    }

    public StackPane getView() {
        return view;
    }

    public Piece getPiece() {
        return piece;
    }

    public void updatePiece(Piece newPiece) {
        this.piece = newPiece;
        view.getChildren().clear();
        createView();
    }
}