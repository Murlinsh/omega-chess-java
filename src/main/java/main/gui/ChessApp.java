package main.gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
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
            // Инициализация игры (по умолчанию CLASSIC)
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

            MenuBar menuBar = createMenuBar();
            root.setTop(menuBar);

            Scene scene = new Scene(root, 1980, 1200);

            try {
                scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
                System.out.println("Стили загружены");
            } catch (Exception e) {
                System.out.println("Не удалось загрузить стили: " + e.getMessage());
            }

            primaryStage.setTitle("Omega Chess - Классический режим");
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(1000);
            primaryStage.setMinHeight(700);
            primaryStage.show();

            infoPanel.updateInfo();

            System.out.println("JavaFX приложение успешно запущено!");
            log("Приложение запущено");

        } catch (Exception e) {
            System.err.println("Ошибка в JavaFX: " + e.getMessage());
            e.printStackTrace();
            showErrorDialog("Ошибка запуска", e.getMessage());
        }
    }

    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();

        Menu gameMenu = new Menu("Игра");

        MenuItem newClassicGame = new MenuItem("Новая игра (Классика 8×8)");
        newClassicGame.setOnAction(e -> changeGameType(GameType.CLASSIC));

        MenuItem newOmegaGame = new MenuItem("Новая игра (Omega 10×10)");
        newOmegaGame.setOnAction(e -> changeGameType(GameType.OMEGA));

        SeparatorMenuItem separator = new SeparatorMenuItem();

        MenuItem exitItem = new MenuItem("Выход");
        exitItem.setOnAction(e -> System.exit(0));

        gameMenu.getItems().addAll(newClassicGame, newOmegaGame, separator, exitItem);

        Menu helpMenu = new Menu("Справка");
        MenuItem aboutItem = new MenuItem("О программе");
        aboutItem.setOnAction(e -> showAboutDialog());

        helpMenu.getItems().add(aboutItem);

        menuBar.getMenus().addAll(gameMenu, helpMenu);
        return menuBar;
    }

    private void printBoardToConsole(Board board) {
        int boardSize = game.getGameType().getBoardSize();

        System.out.print("   ");
        for (int col = 0; col < boardSize; col++) {
            char fileChar = (char) ('a' + col);
            if (boardSize > 8) {
                if (col == 8) fileChar = 'i';
                else if (col == 9) fileChar = 'j';
            }
            System.out.print(fileChar + " ");
        }
        System.out.println();

        System.out.print("  +");
        for (int col = 0; col < boardSize; col++) {
            System.out.print("--");
        }
        System.out.println("+");

        for (int row = boardSize - 1; row >= 0; row--) {
            System.out.print((row + 1) + " |");
            for (int col = 0; col < boardSize; col++) {
                Position pos = new Position(row, col);
                Piece piece = board.getPieceAt(pos);

                if (piece == null) {
                    System.out.print(" .");
                } else {
                    char symbol = getPieceSymbol(piece);
                    if (piece.getColor() == Color.BLACK) {
                        symbol = Character.toLowerCase(symbol);
                    }
                    System.out.print(" " + symbol);
                }
            }
            System.out.println(" | " + (row + 1));
        }

        System.out.print("  +");
        for (int col = 0; col < boardSize; col++) {
            System.out.print("--");
        }
        System.out.println("+");

        System.out.print("   ");
        for (int col = 0; col < boardSize; col++) {
            char fileChar = (char) ('a' + col);
            if (boardSize > 8) {
                if (col == 8) fileChar = 'i';
                else if (col == 9) fileChar = 'j';
            }
            System.out.print(fileChar + " ");
        }
        System.out.println();

        // Выводим информацию об угловых фигурах для OMEGA
        if (game.getGameType() == GameType.OMEGA) {
            System.out.println("\nУгловые фигуры (Omega Chess):");
            Position[] cornerPositions = {
                    new Position(-1, -1, true),
                    new Position(-1, 10, true),
                    new Position(10, -1, true),
                    new Position(10, 10, true)
            };

            String[] cornerNames = {
                    "Белый Чемпион (WC):",
                    "Белый Волшебник (WW):",
                    "Черный Чемпион (BC):",
                    "Черный Волшебник (BW):"
            };

            for (int i = 0; i < cornerPositions.length; i++) {
                Piece piece = board.getPieceAt(cornerPositions[i]);
                if (piece != null) {
                    System.out.println("  " + cornerNames[i] + " " +
                            piece.getClass().getSimpleName() + " " + piece.getColor());
                }
            }
        }
    }

    private char getPieceSymbol(Piece piece) {
        if (piece instanceof King) return 'K';
        else if (piece instanceof Queen) return 'Q';
        else if (piece instanceof Rook) return 'R';
        else if (piece instanceof Bishop) return 'B';
        else if (piece instanceof Knight) return 'N';
        else if (piece instanceof Pawn) return 'P';
        else if (piece instanceof Champion) return 'C';
        else if (piece instanceof Wizard) return 'W';
        else return '?';
    }

    public void newGame(GameType gameType) {
        try {
            log("=== СОЗДАНИЕ НОВОЙ ИГРЫ ===");
            log("Тип игры: " + gameType);
            log("Размер доски: " + gameType.getBoardSize() + "x" + gameType.getBoardSize());

            // 1. СОЗДАЕМ НОВУЮ ИГРУ
            game = new Game(gameType);
            log("Игра создана успешно");

            // 2. СОЗДАЕМ НОВЫЙ ChessBoardView (КРИТИЧЕСКИ ВАЖНО!)
            boardView = new ChessBoardView(game, this);
            log("ChessBoardView создан");

            // 3. ПОЛУЧАЕМ КОРНЕВОЙ КОНТЕЙНЕР И ЗАМЕНЯЕМ ДОСКУ
            BorderPane root = (BorderPane) primaryStage.getScene().getRoot();
            root.setCenter(boardView.getView());
            log("Доска установлена в корневой контейнер");

            // 3.1. ЦЕНТРИРУЕМ ДОСКУ (НОВАЯ СТРОКА)
            // Получаем размеры доступной области
            double availableWidth = root.getWidth() - controlPanel.getView().getWidth();
            double availableHeight = root.getHeight() - infoPanel.getView().getHeight() - 50; // -50 для меню

            boardView.centerBoardInParent(availableWidth, availableHeight);
            log("Доска отцентрирована: " + availableWidth + "x" + availableHeight);

            // 4. ОБНОВЛЯЕМ InfoPanel
            infoPanel.updateGame(game);
            infoPanel.updateInfo();
            log("InfoPanel обновлен");

            // 5. ОБНОВЛЯЕМ ControlPanel
            if (controlPanel != null) {
                controlPanel.updateGameType(gameType);
                controlPanel.log("Новая игра: " +
                        (gameType == GameType.CLASSIC ? "Классические шахматы" : "Omega Chess"));
            }

            // 6. ОТРИСОВЫВАЕМ ДОСКУ
            boardView.drawBoard();
            log("Доска отрисована");

            // 7. ОБНОВЛЯЕМ ЗАГОЛОВОК ОКНА
            String title = "Omega Chess - ";
            if (gameType == GameType.CLASSIC) {
                title += "Классический режим (8×8)";
            } else {
                title += "Omega-режим (10×10 + угловые клетки)";
            }
            primaryStage.setTitle(title);
            log("Заголовок окна обновлен: " + title);

            // 8. ОТЛАДОЧНАЯ ПЕЧАТЬ В КОНСОЛЬ
            System.out.println("\n=== НАЧАЛЬНАЯ ПОЗИЦИЯ ===");
            System.out.println("Тип игры: " + gameType);
            System.out.println("Размер доски: " + gameType.getBoardSize() + "x" + gameType.getBoardSize());

            // Проверяем наличие фигур
            Board board = game.getBoard();
            int pieceCount = 0;

            // Проверяем основную доску
            for (int row = 0; row < gameType.getBoardSize(); row++) {
                for (int col = 0; col < gameType.getBoardSize(); col++) {
                    Position pos = new Position(row, col);
                    Piece piece = board.getPieceAt(pos);
                    if (piece != null) {
                        pieceCount++;
                        if (row == 0 && col == 0) {
                            System.out.println("Пример фигуры: " + piece.getClass().getSimpleName() +
                                    " на " + pos + " цвет: " + piece.getColor());
                        }
                    }
                }
            }

            // Проверяем угловые фигуры для Omega
            if (gameType == GameType.OMEGA) {
                Position[] corners = {
                        new Position(-1, -1, true),
                        new Position(-1, 10, true),
                        new Position(10, -1, true),
                        new Position(10, 10, true)
                };

                for (Position corner : corners) {
                    Piece piece = board.getPieceAt(corner);
                    if (piece != null) {
                        pieceCount++;
                        System.out.println("Угловая фигура: " + piece.getClass().getSimpleName() +
                                " на " + corner + " цвет: " + piece.getColor());
                    }
                }
            }

            System.out.println("Всего фигур на доске: " + pieceCount);
            System.out.println("Текущий игрок: " + game.getCurrentPlayer());
            System.out.println("====================\n");

            // 9. ПРОВЕРЯЕМ РАБОТОСПОСОБНОСТЬ
            log("Новая игра успешно создана");
            log("Текущий игрок: " + (game.getCurrentPlayer() == Color.WHITE ? "Белые" : "Черные"));
            log("Всего фигур: " + pieceCount);

        } catch (Exception e) {
            String errorMsg = "ОШИБКА при создании новой игры: " + e.getMessage();
            log(errorMsg);
            System.err.println(errorMsg);
            e.printStackTrace();

            // Показываем ошибку пользователю
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка");
            alert.setHeaderText("Не удалось создать новую игру");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
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
        } else {
            log("Ход невозможен");
        }
    }

    private String positionToString(Position pos) {
        if (pos == null) return "null";

        if (pos.isCornerCell()) {
            // Специальное обозначение для углов
            if (pos.getRow() == -1 && pos.getCol() == -1) return "WC"; // White Champion
            if (pos.getRow() == -1 && pos.getCol() == 10) return "WW"; // White Wizard
            if (pos.getRow() == 10 && pos.getCol() == -1) return "BC"; // Black Champion
            if (pos.getRow() == 10 && pos.getCol() == 10) return "BW"; // Black Wizard
            return "CORNER";
        }

        int boardSize = game.getGameType().getBoardSize();
        char file = (char) ('a' + pos.getCol());
        // Для 10-колоночной доски корректируем буквы
        if (boardSize > 8) {
            if (pos.getCol() == 8) file = 'i';
            else if (pos.getCol() == 9) file = 'j';
        }
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

        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
        dialog.setTitle("Превращение пешки");
        dialog.setHeaderText("Выберите фигуру для превращения:");

        // Разный текст в зависимости от режима
        if (game.getGameType() == GameType.OMEGA) {
            dialog.setContentText("В какую фигуру превратить пешку? (Omega Chess)");
        } else {
            dialog.setContentText("В какую фигуру превратить пешку?");
        }

        dialog.getButtonTypes().clear();

        ButtonType queenButton = new ButtonType("Ферзь ♕");
        ButtonType rookButton = new ButtonType("Ладья ♖");
        ButtonType bishopButton = new ButtonType("Слон ♗");
        ButtonType knightButton = new ButtonType("Конь ♘");

        dialog.getButtonTypes().addAll(queenButton, rookButton, bishopButton, knightButton);

        // Добавляем дополнительные фигуры для Omega
        if (game.getGameType() == GameType.OMEGA) {
            ButtonType championButton = new ButtonType("Чемпион C");
            ButtonType wizardButton = new ButtonType("Волшебник W");
            dialog.getButtonTypes().addAll(championButton, wizardButton);
        }

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent()) {
            Class<? extends Piece> pieceClass = null;
            String pieceName = "";

            if (result.get() == queenButton) {
                pieceClass = Queen.class;
                pieceName = "ферзя";
            } else if (result.get() == rookButton) {
                pieceClass = Rook.class;
                pieceName = "ладью";
            } else if (result.get() == bishopButton) {
                pieceClass = Bishop.class;
                pieceName = "слона";
            } else if (result.get() == knightButton) {
                pieceClass = Knight.class;
                pieceName = "коня";
            } else if (result.get().getText().contains("Чемпион")) {
                pieceClass = Champion.class;
                pieceName = "чемпиона";
            } else if (result.get().getText().contains("Волшебник")) {
                pieceClass = Wizard.class;
                pieceName = "волшебника";
            }

            if (pieceClass != null) {
                try {
                    game.promotePawn(pawnPosition, pieceClass);
                    boardView.drawBoard();
                    infoPanel.updateInfo();
                    log("Пешка превращена в " + pieceName);
                } catch (Exception e) {
                    log("Ошибка при превращении пешки: " + e.getMessage());
                    showErrorDialog("Ошибка превращения", e.getMessage());
                }
            }
        }
    }

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
        System.out.println("=== СМЕНА ТИПА ИГРЫ на " + newType + " ===");

        if (game != null && !game.isGameOver()) {
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

    private void showAboutDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("О программе");
        alert.setHeaderText("Omega Chess");
        alert.setContentText(
                "Omega Chess - Расширенная версия шахмат\n\n" +
                        "Режимы:\n" +
                        "• Классические шахматы (8×8)\n" +
                        "• Omega Chess (10×10 + 4 угловые клетки)\n\n" +
                        "Новые фигуры в Omega Chess:\n" +
                        "• Чемпион (Champion) - сочетание коня и ладьи\n" +
                        "• Волшебник (Wizard) - сочетание слона и коня\n\n" +
                        "Управление:\n" +
                        "1. Клик на фигуру (подсветится желтым)\n" +
                        "2. Клик на клетку (зеленый - ход, красный - взятие)\n" +
                        "3. Превращение пешки - выбор фигуры в диалоге\n\n" +
                        "Горячие клавиши:\n" +
                        "Ctrl+Z - отмена хода\n" +
                        "F2 - новая игра\n" +
                        "Esc - сдаться\n\n" +
                        "Версия: 1.0 Omega Edition"
        );
        alert.showAndWait();
    }

    // Этот метод НУЖЕН для запуска из командной строки
    public static void main(String[] args) {
        System.out.println("Запуск ChessApp Omega Edition...");
        launch(args);
    }
}