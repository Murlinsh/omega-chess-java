package main;

import java.util.List;
import java.util.Scanner;

public class ConsoleChess {
    private Game game;
    private Scanner scanner;

    public ConsoleChess(GameType gameType) {
        this.game = new Game(gameType);
        this.scanner = new Scanner(System.in);
    }

    public void startGame() {
        System.out.println("=== ШАХМАТЫ ===");
        System.out.println("Команды: 'сдаюсь' - завершить игру, 'отмена' - отменить последний ход");

        while (!game.isGameOver()) {
            printBoard();
            printGameInfo();

            String fromInput = readInput("Откуда (например, e2): ");
            if (fromInput == null) break;

            if (fromInput.equals("отмена")) {
                game.undoLastMove();
                continue;
            }

            Position from = parsePosition(fromInput);
            if (from == null) {
                System.out.println("Некорректный ввод. Формат: e2, a4 (a-h, 1-8) или 'отмена'");
                continue;
            }

            String toInput = readInput("Куда (например, e4): ");
            if (toInput == null) break;

            if (toInput.equals("отмена")) {
                game.undoLastMove();
                continue;
            }

            Position to = parsePosition(toInput);
            if (to == null) {
                System.out.println("Некорректный ввод. Формат: e2, a4 (a-h, 1-8) или 'отмена'");
                continue;
            }

            boolean success = game.makeMove(from, to);
            if (!success) {
                System.out.println("Недопустимый ход! Попробуйте снова.");
            } else {
                if (game.getBoard().getPieceAt(to) instanceof Pawn
                        && game.isPromotionPosition(to, game.getBoard().getPieceAt(to).getColor())) {
                    game.promotePawn(to, askForPromotionChoice());
                }
            }
        }
    }

    private String readInput(String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim().toLowerCase();

        if (input.equals("сдаюсь")) {
            game.surrender();
            return null;
        }

        return input;
    }

    private void printBoard() {
        System.out.println("\n   a b c d e f g h");

        for (int row = 7; row >= 0; row--) {
            System.out.print((row + 1) + "  ");

            for (int col = 0; col < 8; col++) {
                Piece piece = game.getBoard().getPieceAt(new Position(row, col));
                char symbol = getPieceSymbol(piece);
                System.out.print(symbol + " ");
            }

            System.out.println(" " + (row + 1));
        }

        System.out.println("   a b c d e f g h\n");
    }

    private char getPieceSymbol(Piece piece) {
        if (piece == null) return '.';

        char baseSymbol;
        if (piece instanceof King) baseSymbol = 'K';
        else if (piece instanceof Queen) baseSymbol = 'Q';
        else if (piece instanceof Rook) baseSymbol = 'R';
        else if (piece instanceof Bishop) baseSymbol = 'B';
        else if (piece instanceof Knight) baseSymbol = 'N';
        else baseSymbol = 'P';

        return (piece.getColor() == Color.WHITE) ? baseSymbol : Character.toLowerCase(baseSymbol);
    }

    private void printGameInfo() {
        System.out.println("Снятые белые: " + game.getCapturedWhitePieces().size());
        System.out.println("Снятые чёрные: " + game.getCapturedBlackPieces().size());
        System.out.println("Ходят: " + (game.getCurrentPlayer() == Color.WHITE ? "БЕЛЫЕ" : "ЧЁРНЫЕ"));
    }

    private Position parsePosition(String input) {
        if (input.length() != 2) return null;

        char file = input.charAt(0);
        char rank = input.charAt(1);

        if (file < 'a' || file > 'h') return null;
        if (rank < '1' || rank > '8') return null;

        int col = file - 'a';
        int row = rank - '1';

        return new Position(row, col);
    }

    public Class<? extends Piece> askForPromotionChoice() {
        while (true) {
            System.out.println("Выберите фигуру для превращения:");
            if (game.getGameType() == GameType.CLASSIC) {
                System.out.println("Q - Queen, R - Rook, B - Bishop, N - Knight");
            } else {
                System.out.println("Q - Queen, R - Rook, B - Bishop, N - Knight, C - Champion, W - Wizard");
            }

            String input = scanner.nextLine().toUpperCase();
            if (input.equals("Q")) {
                return Queen.class;
            } else if (input.equals("R")) {
                return Rook.class;
            } else if (input.equals("B")) {
                return Bishop.class;
            } else if (input.equals("N")) {
                return Knight.class;
            } else {
                System.out.println("Некорректная буква.");
            }
        }
    }

    private void printResult() {
        // Объявление победителя
    }
}