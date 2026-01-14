package main.gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import main.*;
import main.pieces.*;

import java.util.Optional;

public class ChessApp extends Application {
    private Game game;
    private ChessBoardView boardView;
    private ControlPanel controlPanel;
    private InfoPanel infoPanel;
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        System.out.println("JavaFX приложение запускается...");

        try {
            // Инициализация игры
            game = new Game(GameType.CLASSIC);

            // ОТЛАДКА: печатаем начальную позицию в консоль
            System.out.println("\n=== НАЧАЛЬНАЯ ПОЗИЦИЯ ДОСКИ ===");
            printBoardToConsole(game.getBoard());
            System.out.println("================================\n");

            // Создание компонентов интерфейса
            boardView = new ChessBoardView(game, this);
            controlPanel = new ControlPanel(this);
            infoPanel = new InfoPanel(game);

            // Настройка главной сцены
            BorderPane root = new BorderPane();
            root.setCenter(boardView.getView());
            root.setRight(controlPanel.getView());
            root.setBottom(infoPanel.getView());

            Scene scene = new Scene(root, 1100, 750);

            // Пробуем загрузить стили
            try {
                scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
                System.out.println("Стили загружены");
            } catch (Exception e) {
                System.out.println("Не удалось загрузить стили: " + e.getMessage());
            }

            primaryStage.setTitle("Omega Chess - Классический режим");
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(900);
            primaryStage.setMinHeight(650);
            primaryStage.show();

            // Обновляем отображение
            infoPanel.updateInfo();

            System.out.println("JavaFX приложение успешно запущено!");
            log("Приложение запущено");

        } catch (Exception e) {
            System.err.println("Ошибка в JavaFX: " + e.getMessage());
            e.printStackTrace();
            showErrorDialog("Ошибка запуска", e.getMessage());
        }
    }

    private void printBoardToConsole(Board board) {
        System.out.println("   a b c d e f g h");
        System.out.println("  +---------------+");

        for (int row = 7; row >= 0; row--) {
            System.out.print((row + 1) + " |");
            for (int col = 0; col < 8; col++) {
                Position pos = new Position(row, col);
                Piece piece = board.getPieceAt(pos);

                if (piece == null) {
                    System.out.print(" .");
                } else {
                    char symbol = getPieceSymbol(piece);
                    // Черные фигуры - строчные, белые - заглавные
                    if (piece.getColor() == Color.BLACK) {
                        symbol = Character.toLowerCase(symbol);
                    }
                    System.out.print(" " + symbol);
                }
            }
            System.out.println(" | " + (row + 1));
        }

        System.out.println("  +---------------+");
        System.out.println("   a b c d e f g h");
    }

    private char getPieceSymbol(Piece piece) {
        if (piece instanceof King) return 'K';
        else if (piece instanceof Queen) return 'Q';
        else if (piece instanceof Rook) return 'R';
        else if (piece instanceof Bishop) return 'B';
        else if (piece instanceof Knight) return 'N';
        else if (piece instanceof Pawn) return 'P';
        else return '?';
    }

    public void newGame(GameType gameType) {
        try {
            game = new Game(gameType);
            boardView.updateGame(game);
            infoPanel.updateGame(game);
            boardView.drawBoard();
            infoPanel.updateInfo();
            log("Новая игра начата. Тип: " +
                    (gameType == GameType.CLASSIC ? "Классические" : "Омега"));

            // Обновляем заголовок окна
            if (primaryStage != null) {
                primaryStage.setTitle("Omega Chess - " +
                        (gameType == GameType.CLASSIC ? "Классический режим" : "Омега-режим"));
            }
        } catch (Exception e) {
            log("Ошибка при создании новой игры: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void newGame() {
        newGame(GameType.CLASSIC);
    }

    public void undoMove() {
        if (game != null && !game.isGameOver()) {
            boolean success = game.undoLastMove();
            if (success) {
                boardView.drawBoard();
                infoPanel.updateInfo();
                log("Ход отменен");
            } else {
                log("Нельзя отменить ход (история пуста)");
            }
        } else {
            log("Игра завершена, нельзя отменить ход");
        }
    }

    public void surrender() {
        if (game != null && !game.isGameOver()) {
            // Подтверждение сдачи
            Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
            confirmDialog.setTitle("Сдача");
            confirmDialog.setHeaderText("Вы уверены, что хотите сдаться?");
            confirmDialog.setContentText("Игра будет завершена в пользу противника.");

            Optional<ButtonType> result = confirmDialog.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                game.surrender();
                infoPanel.updateInfo();
                log((game.getCurrentPlayer() == Color.WHITE ? "Белые" : "Черные") + " сдались!");

                // Показываем сообщение о результате
                Alert resultDialog = new Alert(Alert.AlertType.INFORMATION);
                resultDialog.setTitle("Игра завершена");
                resultDialog.setHeaderText("Сдача принята");
                resultDialog.setContentText("Победили " +
                        (game.getCurrentPlayer().opposite() == Color.WHITE ? "белые" : "черные") + "!");
                resultDialog.show();
            }
        } else {
            log("Игра уже завершена");
        }
    }

    public Game getGame() {
        return game;
    }

    public void makeMove(Position from, Position to) {
        if (game == null || game.isGameOver()) {
            log("Игра не инициализирована или завершена");
            return;
        }

        // Только один лог вместо дублирования
        log("Выполнение хода: " + positionToString(from) + " → " + positionToString(to));

        boolean success = game.makeMove(from, to);
        if (success) {
            boardView.drawBoard();
            infoPanel.updateInfo();

            // Проверяем превращение пешки
            Piece piece = game.getBoard().getPieceAt(to);
            if (piece instanceof Pawn && game.isPromotionPosition(to, piece.getColor())) {
                showPromotionDialog(to);
            }

            // Проверяем конечные состояния
            checkGameStatus();
        }
    }

    private String positionToString(Position pos) {
        if (pos == null) return "null";
        char file = (char) ('a' + pos.getCol());
        int rank = pos.getRow() + 1;
        return file + "" + rank;
    }

    private void checkGameStatus() {
        if (game == null || game.isGameOver()) return;

        // Игрок, который должен ходить СЕЙЧАС (после смены хода)
        Color playerToMove = game.getCurrentPlayer();

        if (game.getBoard().isCheckmate(playerToMove)) {
            // Игрок, который должен ходить, под матом
            Color winner = playerToMove.opposite(); // Тот, кто только что сходил

            log("МАТ! Победили " + winner);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Игра завершена");
            alert.setHeaderText("МАТ!");
            alert.setContentText("Победили " +
                    (winner == Color.WHITE ? "белые" : "черные") + "!");
            alert.show();

            game.declareMate(winner);

        } else if (game.getBoard().isStalemate(playerToMove)) {
            // Игрок, который должен ходить, в пате
            log("ПАТ! Ничья");

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Игра завершена");
            alert.setHeaderText("ПАТ!");
            alert.setContentText("Ничья!");
            alert.show();

            game.declareStalemate();
        }
    }

    private void showPromotionDialog(Position pawnPosition) {
        log("Пешка достигла последней горизонтали! Требуется превращение.");

        // Диалог выбора фигуры
        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
        dialog.setTitle("Превращение пешки");
        dialog.setHeaderText("Выберите фигуру для превращения:");
        dialog.setContentText("В какую фигуру превратить пешку?");

        // Убираем стандартные кнопки
        dialog.getButtonTypes().clear();

        // Добавляем кнопки для выбора фигуры
        ButtonType queenButton = new ButtonType("Ферзь ♕");
        ButtonType rookButton = new ButtonType("Ладья ♖");
        ButtonType bishopButton = new ButtonType("Слон ♗");
        ButtonType knightButton = new ButtonType("Конь ♘");

        dialog.getButtonTypes().addAll(queenButton, rookButton, bishopButton, knightButton);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent()) {
            Class<? extends Piece> pieceClass = null;

            if (result.get() == queenButton) {
                pieceClass = Queen.class;
                log("Пешка превращена в ферзя");
            } else if (result.get() == rookButton) {
                pieceClass = Rook.class;
                log("Пешка превращена в ладью");
            } else if (result.get() == bishopButton) {
                pieceClass = Bishop.class;
                log("Пешка превращена в слона");
            } else if (result.get() == knightButton) {
                pieceClass = Knight.class;
                log("Пешка превращена в коня");
            }

            if (pieceClass != null) {
                game.promotePawn(pawnPosition, pieceClass);
                boardView.drawBoard();
                infoPanel.updateInfo();
            }
        }
    }

    // Метод для логирования
    public void log(String message) {
        if (controlPanel != null) {
            controlPanel.log(message);
        }
        System.out.println("LOG: " + message);
    }

    private void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void changeGameType(GameType newType) {
        if (game != null && !game.isGameOver()) {
            // Подтверждение смены типа игры
            Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
            confirmDialog.setTitle("Смена типа игры");
            confirmDialog.setHeaderText("Текущая игра будет завершена");
            confirmDialog.setContentText("Вы уверены, что хотите сменить тип игры? Текущая игра будет потеряна.");

            Optional<ButtonType> result = confirmDialog.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                newGame(newType);
            }
        } else {
            newGame(newType);
        }
    }

    public InfoPanel getInfoPanel() {
        return infoPanel;
    }

    // Этот метод НУЖЕН для запуска из командной строки
    public static void main(String[] args) {
        System.out.println("Запуск ChessApp...");
        launch(args);
    }
}