package main.gui;

import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;
import javafx.scene.control.Alert;
import main.*;
import main.pieces.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChessBoardView {
    // Размеры клеток для разных режимов
    private static final int CLASSIC_SQUARE_SIZE = 70;
    private static final int OMEGA_SQUARE_SIZE = 65; // Немного уменьшаем для 10x10
    private static final int CORNER_SQUARE_SIZE = 55; // Размер угловых клеток
    private int SQUARE_SIZE; // Текущий размер клетки
    private int BOARD_SIZE;

    // Цвета для разных типов клеток
    private static final Color LIGHT_SQUARE_COLOR = Color.rgb(240, 217, 181);
    private static final Color DARK_SQUARE_COLOR = Color.rgb(181, 136, 99);
    private static final Color CORNER_LIGHT_COLOR = Color.rgb(200, 230, 255);
    private static final Color CORNER_DARK_COLOR = Color.rgb(150, 180, 255);

    // Цвета для выделения
    private static final Color SELECTED_COLOR = Color.rgb(255, 235, 59, 0.8);
    private static final Color POSSIBLE_MOVE_COLOR = Color.rgb(129, 199, 132, 0.7);
    private static final Color POSSIBLE_CAPTURE_COLOR = Color.rgb(239, 83, 80, 0.7);

    private Game game;
    private final ChessApp app;
    private Pane rootPane;
    private GridPane mainGrid;
    private final Map<Position, StackPane> cells;
    private final Map<Position, PieceView> pieceViews;
    private Position selectedPosition = null;
    private List<Position> possibleMoves = null;

    private boolean isProcessingClick = false;

    // Позиционирование доски
    private double boardStartX;  // Начало основной доски по X
    private double boardStartY;  // Начало основной доски по Y
    private double mainBoardWidth;  // Ширина основной доски
    private double mainBoardHeight; // Высота основной доски

    public ChessBoardView(Game game, ChessApp app) {
        this.game = game;
        this.app = app;
        this.BOARD_SIZE = game.getGameType().getBoardSize();

        // Устанавливаем размер клетки в зависимости от типа игры
        if (game.getGameType() == GameType.CLASSIC) {
            this.SQUARE_SIZE = CLASSIC_SQUARE_SIZE;
        } else {
            this.SQUARE_SIZE = OMEGA_SQUARE_SIZE;
        }

        // Рассчитываем размеры основной доски
        this.mainBoardWidth = BOARD_SIZE * SQUARE_SIZE;
        this.mainBoardHeight = BOARD_SIZE * SQUARE_SIZE;

        this.cells = new HashMap<>();
        this.pieceViews = new HashMap<>();
        initializeBoard();
    }

    private void initializeBoard() {
        rootPane = new Pane();
        rootPane.setStyle("-fx-background-color: #8B4513; -fx-padding: 20;");

        app.log("=== Инициализация доски " + BOARD_SIZE + "x" + BOARD_SIZE + " ===");
        app.log("Размер клетки: " + SQUARE_SIZE + "px");

        // Создаем основную доску
        mainGrid = new GridPane();
        mainGrid.setStyle("-fx-background-color: transparent;");

        // Инициализируем позиции (будут установлены при центрировании)
        boardStartX = 0;
        boardStartY = 0;
        mainGrid.setLayoutX(boardStartX);
        mainGrid.setLayoutY(boardStartY);

        // Создаем клетки основной доски
        for (int gridRow = 0; gridRow < BOARD_SIZE; gridRow++) {
            final int chessRow = BOARD_SIZE - 1 - gridRow;

            for (int col = 0; col < BOARD_SIZE; col++) {
                Position pos = new Position(chessRow, col);
                createMainSquare(pos, gridRow, col);
            }
        }

        rootPane.getChildren().add(mainGrid);

        // Если это OMEGA режим - добавляем угловые клетки
        if (game.getGameType() == GameType.OMEGA) {
            createCornerSquares();
        }

        addBoardCoordinates();
        drawBoard();
        app.log("=== Доска инициализирована ===");
    }

    private void createMainSquare(Position pos, int gridRow, int gridCol) {
        Rectangle square = new Rectangle(SQUARE_SIZE, SQUARE_SIZE);

        square.setFill(Board.isLightSquare(pos)
                ? LIGHT_SQUARE_COLOR
                : DARK_SQUARE_COLOR);
        square.setStroke(Color.rgb(93, 64, 55));
        square.setStrokeWidth(1);

        StackPane cellStack = new StackPane();
        cellStack.getChildren().add(square);
        cellStack.setPrefSize(SQUARE_SIZE, SQUARE_SIZE);

        StackPane pieceContainer = new StackPane();
        pieceContainer.setPrefSize(SQUARE_SIZE, SQUARE_SIZE);
        pieceContainer.setMouseTransparent(true);
        cellStack.getChildren().add(pieceContainer);

        final Position cellPos = pos;
        cellStack.setOnMouseClicked(e -> {
            if (!isProcessingClick) {
                isProcessingClick = true;
                try {
                    handleSquareClick(cellPos);
                } finally {
                    new Thread(() -> {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException ex) {
                            Thread.currentThread().interrupt();
                        }
                        javafx.application.Platform.runLater(() -> {
                            isProcessingClick = false;
                        });
                    }).start();
                }
            }
        });

        mainGrid.add(cellStack, gridCol, gridRow);
        cells.put(pos, cellStack);
    }

    private void createCornerSquares() {
        if (game.getGameType() != GameType.OMEGA) return;

        double boardX = mainGrid.getLayoutX();
        double boardY = mainGrid.getLayoutY();
        double boardWidth = 10 * SQUARE_SIZE;
        double boardHeight = 10 * SQUARE_SIZE;

        createCornerSquare(
                new Position(-1, -1, true),
                boardX - CORNER_SQUARE_SIZE,
                boardY + boardHeight,
                "w1"
        );

        createCornerSquare(
                new Position(-1, 10, true),
                boardX + boardWidth,
                boardY + boardHeight,
                "w2"
        );

        createCornerSquare(
                new Position(10, -1, true),
                boardX - CORNER_SQUARE_SIZE,
                boardY - CORNER_SQUARE_SIZE,
                "w3"
        );

        createCornerSquare(
                new Position(10, 10, true),
                boardX + boardWidth,
                boardY - CORNER_SQUARE_SIZE,
                "w4"
        );
    }

    private void createCornerSquare(Position pos, double x, double y, String label) {
        // Проверяем, не создана ли уже эта угловая клетка
        if (cells.containsKey(pos)) {
            // Обновляем позицию существующей клетки
            StackPane existingCell = cells.get(pos);
            existingCell.setLayoutX(x);
            existingCell.setLayoutY(y);
            return;
        }

        Rectangle square = new Rectangle(CORNER_SQUARE_SIZE, CORNER_SQUARE_SIZE);

        square.setFill((pos.getRow() + pos.getCol()) % 2 == 0
                ? CORNER_DARK_COLOR
                : CORNER_LIGHT_COLOR);
        square.setStroke(Color.rgb(70, 100, 150));
        square.setStrokeWidth(2);
        square.setArcWidth(10);
        square.setArcHeight(10);

        StackPane cellStack = new StackPane();
        cellStack.getChildren().add(square);
        cellStack.setPrefSize(CORNER_SQUARE_SIZE, CORNER_SQUARE_SIZE);
        cellStack.setLayoutX(x);
        cellStack.setLayoutY(y);

        StackPane pieceContainer = new StackPane();
        pieceContainer.setPrefSize(CORNER_SQUARE_SIZE, CORNER_SQUARE_SIZE);
        pieceContainer.setMouseTransparent(true);
        cellStack.getChildren().add(pieceContainer);

        // Добавляем подпись w1-w4
        Text cornerLabel = new Text(label);
        cornerLabel.setStyle("-fx-font-size: 11; -fx-font-weight: bold;");
        cornerLabel.setFill(Color.rgb(40, 40, 40));
        StackPane labelPane = new StackPane(cornerLabel);
        labelPane.setLayoutX(3);
        labelPane.setLayoutY(3);
        cellStack.getChildren().add(labelPane);

        final Position cellPos = pos;
        cellStack.setOnMouseClicked(e -> {
            if (!isProcessingClick) {
                isProcessingClick = true;
                try {
                    handleSquareClick(cellPos);
                } finally {
                    new Thread(() -> {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException ex) {
                            Thread.currentThread().interrupt();
                        }
                        javafx.application.Platform.runLater(() -> {
                            isProcessingClick = false;
                        });
                    }).start();
                }
            }
        });

        rootPane.getChildren().add(cellStack);
        cells.put(pos, cellStack);
    }

    private void updateCornerPositions() {
        if (game.getGameType() != GameType.OMEGA) return;

        double boardX = mainGrid.getLayoutX();
        double boardY = mainGrid.getLayoutY();
        double boardWidth = 10 * SQUARE_SIZE;
        double boardHeight = 10 * SQUARE_SIZE;

        Position w1Pos = new Position(-1, -1, true);
        StackPane w1Cell = cells.get(w1Pos);
        if (w1Cell != null) {
            w1Cell.setLayoutX(boardX - CORNER_SQUARE_SIZE);
            w1Cell.setLayoutY(boardY + boardHeight);
        }

        Position w2Pos = new Position(-1, 10, true);
        StackPane w2Cell = cells.get(w2Pos);
        if (w2Cell != null) {
            w2Cell.setLayoutX(boardX + boardWidth);
            w2Cell.setLayoutY(boardY + boardHeight);
        }

        Position w3Pos = new Position(10, -1, true);
        StackPane w3Cell = cells.get(w3Pos);
        if (w3Cell != null) {
            w3Cell.setLayoutX(boardX - CORNER_SQUARE_SIZE);
            w3Cell.setLayoutY(boardY - CORNER_SQUARE_SIZE);
        }

        Position w4Pos = new Position(10, 10, true);
        StackPane w4Cell = cells.get(w4Pos);
        if (w4Cell != null) {
            w4Cell.setLayoutX(boardX + boardWidth);
            w4Cell.setLayoutY(boardY - CORNER_SQUARE_SIZE);
        }
    }

    private void addBoardCoordinates() {
        // Очищаем старые координаты
        rootPane.getChildren().removeIf(node -> {
            if (node instanceof Text) {
                Object userData = node.getUserData();
                return userData != null && userData.equals("coordinate");
            }
            return false;
        });

        // Буквы по горизонтали (a-j)
        for (int col = 0; col < BOARD_SIZE; col++) {
            char fileChar = getFileChar(col);

            Text colLabel = new Text(String.valueOf(fileChar));
            colLabel.setStyle("-fx-font-size: 14; -fx-font-weight: bold;");
            colLabel.setFill(Color.WHITE);
            colLabel.setUserData("coordinate");

            double x = boardStartX + col * SQUARE_SIZE + SQUARE_SIZE / 2 - 5;
            double y = boardStartY + mainBoardHeight + 25;

            colLabel.setLayoutX(x);
            colLabel.setLayoutY(y);
            rootPane.getChildren().add(colLabel);
        }

        // Цифры по вертикали (1-10)
        for (int row = 0; row < BOARD_SIZE; row++) {
            Text rowLabel = new Text(String.valueOf(BOARD_SIZE - row));
            rowLabel.setStyle("-fx-font-size: 14; -fx-font-weight: bold;");
            rowLabel.setFill(Color.WHITE);
            rowLabel.setUserData("coordinate");

            double x = boardStartX - 20;
            double y = boardStartY + row * SQUARE_SIZE + SQUARE_SIZE / 2 + 5;

            rowLabel.setLayoutX(x);
            rowLabel.setLayoutY(y);
            rootPane.getChildren().add(rowLabel);
        }
    }

    private char getFileChar(int col) {
        char fileChar = (char) ('a' + col);
        if (BOARD_SIZE > 8) {
            if (col == 8) fileChar = 'i';
            else if (col == 9) fileChar = 'j';
        }
        return fileChar;
    }

    private void handleSquareClick(Position position) {
        if (game == null || app == null || game.isGameOver()) {
            app.log("ERROR: Игра не инициализирована или завершена");
            return;
        }

        app.log("Клик на: " + positionToString(position) +
                (position.isCornerCell() ? " (угловая)" : ""));

        Piece piece = game.getBoard().getPieceAt(position);
        app.log("Фигура на клетке: " + (piece == null ? "нет" :
                piece.getClass().getSimpleName() + " " + piece.getColor()));
        app.log("Текущий игрок: " + game.getCurrentPlayer());

        if (selectedPosition == null) {
            if (piece != null && piece.getColor() == game.getCurrentPlayer()) {
                selectedPosition = position;
                possibleMoves = piece.getPossibleMoves(game.getBoard());
                app.log("Все возможные ходы фигуры: " + possibleMoves.size());

                possibleMoves.removeIf(move -> !game.getBoard().isMoveLegal(position, move, piece.getColor()));
                app.log("Легальные ходы после фильтрации: " + possibleMoves.size());

                highlightSquare(position);
                highlightPossibleMoves();
            } else {
                app.log("Нельзя выбрать: " +
                        (piece == null ? "клетка пуста" :
                                "фигура другого цвета (" + piece.getColor() + ")"));
            }
        } else {
            if (possibleMoves != null && possibleMoves.contains(position)) {
                app.log("Ход: " + positionToString(selectedPosition) + " → " + positionToString(position));

                main.Color currentPlayerBeforeMove = game.getCurrentPlayer();
                app.makeMove(selectedPosition, position);

                clearHighlights();
                selectedPosition = null;
                possibleMoves = null;

                main.Color playerToCheck = game.getCurrentPlayer();
                checkForGameEnd(playerToCheck, currentPlayerBeforeMove);

            } else {
                app.log("Отмена выбора");
                clearHighlights();
                selectedPosition = null;
                possibleMoves = null;
            }
        }
    }

    private String positionToString(Position pos) {
        if (pos == null) return "null";

        if (pos.isCornerCell()) {
            if (pos.getRow() == -1 && pos.getCol() == -1) return "w1";
            if (pos.getRow() == -1 && pos.getCol() == 10) return "w2";
            if (pos.getRow() == 10 && pos.getCol() == -1) return "w3";
            if (pos.getRow() == 10 && pos.getCol() == 10) return "w4";
        }

        char file = getFileChar(pos.getCol());
        int rank = pos.getRow() + 1;
        return file + "" + rank;
    }

    private void checkForGameEnd(main.Color playerToCheck, main.Color opponent) {
        Board board = game.getBoard();

        boolean hasLegalMoves = board.hasLegalMoves(playerToCheck);
        boolean kingInCheck = board.isKingInCheck(playerToCheck);

        if (!hasLegalMoves) {
            if (kingInCheck) {
                app.log("МАТ! Король " + (playerToCheck == main.Color.WHITE ? "белых" : "чёрных") +
                        " под шахом без легальных ходов");
                game.declareMate(opponent);
                showMateDialog(playerToCheck);
            } else {
                app.log("ПАТ! У " + (playerToCheck == main.Color.WHITE ? "белых" : "чёрных") +
                        " нет легальных ходов, но король не под шахом");
                game.declareStalemate();
                showStalemateDialog();
            }
        } else if (kingInCheck) {
            app.log("ШАХ королю " + (playerToCheck == main.Color.WHITE ? "белых" : "чёрных"));
        }
    }

    private void showMateDialog(main.Color loser) {
        javafx.application.Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Игра завершена");
            alert.setHeaderText("ШАХ И МАТ!");
            alert.setContentText("🎉 ПОБЕДА " +
                    (loser.opposite() == main.Color.WHITE ? "БЕЛЫХ" : "ЧЁРНЫХ") + "!\n\n" +
                    "♚ Король " + (loser == main.Color.WHITE ? "белых" : "чёрных") + " под матом\n" +
                    "⏱ Игра длилась: " + game.getMoveCount() + " ходов");
            alert.show();
        });
    }

    private void showStalemateDialog() {
        javafx.application.Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Игра завершена");
            alert.setHeaderText("ПАТ - НИЧЬЯ!");
            alert.setContentText(
                    "⚖️ ИГРА ЗАВЕРШИЛАСЬ ВНИЧЬЮ\n\n" +
                            "🔒 Нет легальных ходов\n" +
                            "♔ Король не под шахом\n" +
                            "⏱ Игра длилась: " + game.getMoveCount() + " ходов"
            );
            alert.show();
        });
    }

    private void highlightSquare(Position position) {
        StackPane cell = cells.get(position);
        if (cell != null && !cell.getChildren().isEmpty()) {
            Rectangle square = (Rectangle) cell.getChildren().get(0);
            square.setFill(SELECTED_COLOR);
        } else {
            app.log("ERROR: Не найдена клетка для позиции " + positionToString(position));
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
                        square.setFill(POSSIBLE_CAPTURE_COLOR);
                    } else {
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

                if (pos.isCornerCell()) {
                    square.setFill((pos.getRow() + pos.getCol()) % 2 == 0
                            ? CORNER_DARK_COLOR
                            : CORNER_LIGHT_COLOR);
                } else {
                    square.setFill(Board.isLightSquare(pos)
                            ? LIGHT_SQUARE_COLOR
                            : DARK_SQUARE_COLOR);
                }
            }
        }
    }

    public void drawBoard() {
        // Полностью очищаем фигуры
        for (StackPane cell : cells.values()) {
            while (cell.getChildren().size() > 1) {
                cell.getChildren().remove(1);
            }
        }

        pieceViews.clear();

        // Добавляем фигуры с основной доски
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
                    }
                }
            }
        }

        // Добавляем угловые фигуры (только для OMEGA)
        if (game.getGameType() == GameType.OMEGA) {
            Position[] cornerPositions = {
                    new Position(-1, -1, true),
                    new Position(-1, 10, true),
                    new Position(10, -1, true),
                    new Position(10, 10, true)
            };

            for (Position pos : cornerPositions) {
                Piece piece = game.getBoard().getPieceAt(pos);
                if (piece != null) {
                    StackPane cell = cells.get(pos);
                    if (cell != null) {
                        PieceView pieceView = new PieceView(piece);
                        pieceViews.put(pos, pieceView);
                        cell.getChildren().add(pieceView.getView());

                        if (cell.getChildren().size() > 1) {
                            javafx.scene.Node pieceNode = cell.getChildren().get(1);
                            pieceNode.setScaleX(0.8);
                            pieceNode.setScaleY(0.8);
                        }
                    }
                }
            }
        }
    }

    public Pane getView() {
        return rootPane;
    }

    public void updateGame(Game newGame) {
        this.game = newGame;
        this.BOARD_SIZE = game.getGameType().getBoardSize();

        // Обновляем размер клетки в зависимости от типа игры
        if (game.getGameType() == GameType.CLASSIC) {
            this.SQUARE_SIZE = CLASSIC_SQUARE_SIZE;
        } else {
            this.SQUARE_SIZE = OMEGA_SQUARE_SIZE;
        }

        selectedPosition = null;
        possibleMoves = null;

        rootPane.getChildren().clear();
        cells.clear();
        pieceViews.clear();

        initializeBoard();
    }

    // Метод для центрирования доски в родительском контейнере
    public void centerBoardInParent(double parentWidth, double parentHeight) {
        // Рассчитываем общие размеры
        double totalWidth = mainBoardWidth;
        double totalHeight = mainBoardHeight;

        if (game.getGameType() == GameType.OMEGA) {
            // Для Omega Chess добавляем угловые клетки
            // Общая доска 12×12 (10 основных + 2 угловых с каждой стороны)
            totalWidth = mainBoardWidth + 2 * CORNER_SQUARE_SIZE;
            totalHeight = mainBoardHeight + 2 * CORNER_SQUARE_SIZE;
        }

        // Вычисляем отступы для центрирования
        double offsetX = (parentWidth - totalWidth) / 2;
        double offsetY = (parentHeight - totalHeight) / 2;

        // Позиционируем основную доску
        if (game.getGameType() == GameType.OMEGA) {
            // Для Omega: основная доска смещена на размер угловых клеток
            mainGrid.setLayoutX(offsetX + CORNER_SQUARE_SIZE);
            mainGrid.setLayoutY(offsetY + CORNER_SQUARE_SIZE);
        } else {
            // Для Classic: основная доска по центру
            mainGrid.setLayoutX(offsetX);
            mainGrid.setLayoutY(offsetY);
        }

        // Обновляем boardStartX и boardStartY
        boardStartX = mainGrid.getLayoutX();
        boardStartY = mainGrid.getLayoutY();

        // Обновляем координаты (убираем старые, добавляем новые)
        rootPane.getChildren().removeIf(node -> {
            if (node instanceof Text) {
                Object userData = node.getUserData();
                return userData != null && userData.equals("coordinate");
            }
            return false;
        });

        // Обновляем угловые клетки (если есть)
        if (game.getGameType() == GameType.OMEGA) {
            updateCornerPositions();
        }

        // Добавляем координаты доски
        addBoardCoordinates();

        // Перерисовываем фигуры
        drawBoard();
    }
}