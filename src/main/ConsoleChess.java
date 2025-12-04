package main;
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
        System.out.println("Команды: 'сдаюсь' - завершить игру");

        while (!game.isGameOver()) {
            printBoard();
            printGameInfo();

            Position from = readPosition("Откуда (например, e2): ");
            if (from == null) break; // Игрок сдался

            Position to = readPosition("Куда (например, e4): ");
            if (to == null) break; // Игрок сдался

            boolean success = game.makeMove(from, to);
            if (!success) {
                System.out.println("Недопустимый ход! Попробуйте снова.");
            }
        }
    }

    private void printBoard() {
        System.out.println("\n   a b c d e f g h");

        for (int row = 7; row >= 0; row--) { // от 8-го ряда к 1-му
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

        // Белые = заглавные, чёрные = строчные
        return (piece.getColor() == Color.WHITE) ? baseSymbol : Character.toLowerCase(baseSymbol);
    }

    private void printGameInfo() {
        System.out.println("Снятые белые: " + game.getCapturedWhitePieces().size());
        System.out.println("Снятые чёрные: " + game.getCapturedBlackPieces().size());
        System.out.println("Ходят: " + (game.getCurrentPlayer() == Color.WHITE ? "БЕЛЫЕ" : "ЧЁРНЫЕ"));
    }

    private Position readPosition(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim().toLowerCase();

            if (input.equals("сдаюсь")) {
                game.surrender();
                return null;
            }

            Position pos = parsePosition(input);
            if (pos != null && pos.isValid(8)) {
                return pos;
            }
            System.out.println("Некорректный ввод. Формат: e2, a4 (a-h, 1-8)");
        }
    }

    private Position parsePosition(String input) {
        if (input.length() != 2) return null;

        char file = input.charAt(0); // 'a'-'h'
        char rank = input.charAt(1); // '1'-'8'

        int col = file - 'a'; // a=0, b=1, ..., h=7
        int row = rank - '1'; // 1=0, 2=1, ..., 8=7

        // В твоей системе a1=[0][0], так что row=rank-1
        return new Position(row, col);
    }

    private void printResult() {
        // Объявление победителя
    }
}