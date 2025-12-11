package main.gui;

import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;
import main.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChessBoardView {
    private static final int SQUARE_SIZE = 70;
    private static final int BOARD_SIZE = 8;

    // Классические цвета шахматной доски
    private static final Color LIGHT_SQUARE_COLOR = Color.rgb(240, 217, 181); // #f0d9b5
    private static final Color DARK_SQUARE_COLOR = Color.rgb(181, 136, 99);   // #b58863

    // Цвета для выделения
    private static final Color SELECTED_COLOR = Color.rgb(255, 235, 59, 0.8);    // Желтый с прозрачностью
    private static final Color POSSIBLE_MOVE_COLOR = Color.rgb(129, 199, 132, 0.7); // Зеленый с прозрачностью
    private static final Color POSSIBLE_CAPTURE_COLOR = Color.rgb(239, 83, 80, 0.7); // Красный с прозрачностью

    private Game game;
    private final ChessApp app;
    private GridPane grid;
    private final Map<Position, StackPane> cells;
    private final Map<Position, PieceView> pieceViews;
    private Position selectedPosition = null;
    private List<Position> possibleMoves = null;

    public ChessBoardView(Game game, ChessApp app) {
        this.game = game;
        this.app = app;
        this.cells = new HashMap<>();
        this.pieceViews = new HashMap<>();
        initializeBoard();
    }

    private void initializeBoard() {
        grid = new GridPane();
        grid.setStyle("-fx-background-color: #8B4513; -fx-padding: 10; -fx-border-color: #654321; -fx-border-width: 5;");

        app.log("=== Инициализация доски ===");

        for (int gridRow = 0; gridRow < BOARD_SIZE; gridRow++) {
            final int currentGridRow = gridRow;
            final int chessRow = 7 - gridRow;

            for (int col = 0; col < BOARD_SIZE; col++) {
                final int currentCol = col;
                Position pos = new Position(chessRow, col);

                Rectangle square = new Rectangle(SQUARE_SIZE, SQUARE_SIZE);

                // Используем классические цвета шахматной доски
                square.setFill(Board.isLightSquare(pos)
                        ? LIGHT_SQUARE_COLOR
                        : DARK_SQUARE_COLOR);
                square.setStroke(Color.rgb(93, 64, 55)); // Темно-коричневая обводка
                square.setStrokeWidth(1);

                StackPane cellStack = new StackPane();
                cellStack.getChildren().add(square);
                cellStack.setPrefSize(SQUARE_SIZE, SQUARE_SIZE);

                final Position cellPos = pos;
                cellStack.setOnMouseClicked(e -> {
                    app.log("Клик по клетке: " + positionToChessNotation(cellPos) +
                            " (Grid: " + currentGridRow + "," + currentCol +
                            ", Chess: " + chessRow + "," + currentCol + ")");
                    handleSquareClick(cellPos);
                });

                grid.add(cellStack, col, gridRow);
                cells.put(pos, cellStack);

                Piece piece = game.getBoard().getPieceAt(pos);
                if (piece != null) {
                    app.log("Создана клетка: " + positionToChessNotation(pos) +
                            " - " + piece.getClass().getSimpleName() + " " + piece.getColor());
                }
            }
        }

        addBoardCoordinates();
        drawBoard();
        app.log("=== Доска инициализирована ===");
    }

    private void addBoardCoordinates() {
        // Добавляем буквы (a-h) внизу
        for (int col = 0; col < BOARD_SIZE; col++) {
            Text colLabel = new Text(String.valueOf((char) ('a' + col)));
            colLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");
            colLabel.setFill(Color.WHITE);
            grid.add(colLabel, col, BOARD_SIZE);
        }

        // Добавляем цифры (1-8) справа
        for (int row = 0; row < BOARD_SIZE; row++) {
            Text rowLabel = new Text(String.valueOf(row + 1));
            rowLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");
            rowLabel.setFill(Color.WHITE);
            grid.add(rowLabel, BOARD_SIZE, BOARD_SIZE - 1 - row);
        }
    }

    private void handleSquareClick(Position position) {
        if (game == null || app == null) return;

        app.log("=== ОБРАБОТКА КЛИКА ===");
        app.log("Позиция: " + positionToChessNotation(position) +
                " (row=" + position.getRow() + ", col=" + position.getCol() + ")");

        Piece piece = game.getBoard().getPieceAt(position);

        if (piece == null) {
            app.log("На этой позиции нет фигуры");
        } else {
            app.log("На позиции: " + piece.getClass().getSimpleName() +
                    ", цвет: " + piece.getColor() +
                    ", текущий игрок: " + game.getCurrentPlayer());
        }

        if (selectedPosition == null) {
            if (piece != null && piece.getColor() == game.getCurrentPlayer()) {
                selectedPosition = position;

                // Получаем возможные ходы
                possibleMoves = piece.getPossibleMoves(game.getBoard());

                // ФИЛЬТРУЕМ: убираем ходы, которые оставляют короля под шахом
                possibleMoves.removeIf(move -> !game.getBoard().isMoveLegal(position, move, piece.getColor()));

                app.log("Выбрана " + getPieceName(piece) +
                        " на " + positionToChessNotation(position));
                app.log("Количество возможных ходов (после фильтрации шаха): " + possibleMoves.size());

                for (Position move : possibleMoves) {
                    Piece target = game.getBoard().getPieceAt(move);
                    app.log("  → " + positionToChessNotation(move) +
                            (target != null ? " (взятие " + getPieceName(target) + ")" : ""));
                }

                highlightSquare(position);
                highlightPossibleMoves();
            } else if (piece != null) {
                app.log("Это фигура противника!");
            } else {
                app.log("Пустая клетка");
            }
        } else {
            if (possibleMoves != null && possibleMoves.contains(position)) {
                app.log("Выполняем ход с " + positionToChessNotation(selectedPosition) +
                        " на " + positionToChessNotation(position));

                app.makeMove(selectedPosition, position);

                clearHighlights();
                selectedPosition = null;
                possibleMoves = null;

            } else if (piece != null && piece.getColor() == game.getCurrentPlayer()) {
                app.log("Выбрана новая фигура: " + getPieceName(piece));
                clearHighlights();
                selectedPosition = position;
                possibleMoves = piece.getPossibleMoves(game.getBoard());

                // ФИЛЬТРУЕМ: убираем ходы, которые оставляют короля под шахом
                possibleMoves.removeIf(move -> !game.getBoard().isMoveLegal(position, move, piece.getColor()));

                highlightSquare(position);
                highlightPossibleMoves();
            } else {
                app.log("Отмена выбора фигуры");
                clearHighlights();
                selectedPosition = null;
                possibleMoves = null;
            }
        }
    }

    private void highlightSquare(Position position) {
        StackPane cell = cells.get(position);
        if (cell != null && !cell.getChildren().isEmpty()) {
            Rectangle square = (Rectangle) cell.getChildren().get(0);
            square.setFill(SELECTED_COLOR);
        } else {
            app.log("ERROR: Не найдена клетка для позиции " + positionToChessNotation(position));
        }
    }

    private void highlightPossibleMoves() {
        if (possibleMoves != null) {
            for (Position move : possibleMoves) {
                StackPane cell = cells.get(move);
                if (cell != null && !cell.getChildren().isEmpty()) {
                    Rectangle square = (Rectangle) cell.getChildren().get(0);

                    Piece target = game.getBoard().getPieceAt(move);
                    if (target != null && target.getColor() != game.getCurrentPlayer()) {
                        // Взятие - красный цвет
                        square.setFill(POSSIBLE_CAPTURE_COLOR);
                    } else {
                        // Обычный ход - зеленый цвет
                        square.setFill(POSSIBLE_MOVE_COLOR);
                    }
                }
            }
        }
    }

    private void clearHighlights() {
        for (Map.Entry<Position, StackPane> entry : cells.entrySet()) {
            Position pos = entry.getKey();
            StackPane cell = entry.getValue();

            if (!cell.getChildren().isEmpty()) {
                Rectangle square = (Rectangle) cell.getChildren().get(0);

                // Восстанавливаем оригинальные цвета клеток
                square.setFill(Board.isLightSquare(pos)
                        ? LIGHT_SQUARE_COLOR
                        : DARK_SQUARE_COLOR);
            }
        }
    }

    public void drawBoard() {
        app.log("=== ОТРИСОВКА ФИГУР ===");

        // 1. Полностью очищаем ВСЕ фигуры со ВСЕХ клеток
        for (StackPane cell : cells.values()) {
            // Оставляем только первый элемент (прямоугольник-фон)
            while (cell.getChildren().size() > 1) {
                cell.getChildren().remove(1);
            }
        }

        // Очищаем карту pieceViews
        pieceViews.clear();

        // 2. Добавляем фигуры на их текущие позиции
        for (int chessRow = 0; chessRow < BOARD_SIZE; chessRow++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                Position pos = new Position(chessRow, col);
                Piece piece = game.getBoard().getPieceAt(pos);

                if (piece != null) {
                    StackPane cell = cells.get(pos);
                    if (cell != null) {
                        PieceView pieceView = new PieceView(piece);
                        pieceViews.put(pos, pieceView);
                        cell.getChildren().add(pieceView.getView());

                        app.log("Добавлена фигура: " + piece.getClass().getSimpleName() +
                                " на " + positionToChessNotation(pos));
                    } else {
                        app.log("ERROR: Не найдена клетка для " + positionToChessNotation(pos));
                    }
                }
            }
        }

        app.log("=== ФИГУРЫ ОТРИСОВАНЫ ===");
    }

    private Position findPositionByPieceView(PieceView pieceView) {
        for (Map.Entry<Position, PieceView> entry : pieceViews.entrySet()) {
            if (entry.getValue() == pieceView) {
                return entry.getKey();
            }
        }
        return null;
    }

    public GridPane getView() {
        return grid;
    }

    public void updateGame(Game newGame) {
        this.game = newGame;
        selectedPosition = null;
        possibleMoves = null;
        drawBoard();
    }

    private String getPieceName(Piece piece) {
        if (piece instanceof King) return "король";
        else if (piece instanceof Queen) return "ферзь";
        else if (piece instanceof Rook) return "ладья";
        else if (piece instanceof Bishop) return "слон";
        else if (piece instanceof Knight) return "конь";
        else if (piece instanceof Pawn) return "пешка";
        else return "фигура";
    }

    private String positionToChessNotation(Position pos) {
        char file = (char) ('a' + pos.getCol());
        int rank = pos.getRow() + 1;
        return file + "" + rank;
    }
}