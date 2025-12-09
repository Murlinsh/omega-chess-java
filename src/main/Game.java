package main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Game {
    private GameType gameType;
    private Board board;
    private Color currentPlayer;
    private boolean isGameOver;

    private List<Piece> capturedWhitePieces = new ArrayList<>();
    private List<Piece> capturedBlackPieces = new ArrayList<>();

    public Game(GameType gameType) {
        this.gameType = gameType;
        this.board = new Board(this);
        this.currentPlayer = Color.WHITE;
        this.board.initializePieces();
    }

    private Piece createPiece(Class<? extends Piece> pieceType, Color color, Position pos) {
        if (pieceType == Queen.class) return new Queen(color, pos);
        if (pieceType == Rook.class) return new Rook(color, pos);
        if (pieceType == Bishop.class) return new Bishop(color, pos);
        if (pieceType == Knight.class) return new Knight(color, pos);
        if (pieceType == King.class) return new King(color, pos);
        if (pieceType == Pawn.class) return new Pawn(color, pos);

//        // Для OMEGA-шахмат:
//        if (gameType == GameType.OMEGA) {
//            if (pieceType == Champion.class) return new Champion(color, pos);
//            if (pieceType == Wizard.class) return new Wizard(color, pos);
//        }

        throw new IllegalArgumentException("Unknown piece type: " + pieceType);
    }

    public boolean makeMove(Position from, Position to) {
        if (isGameOver) {
            System.out.println("Игра завершена");
            return false;
        }

        Piece piece = board.getPieceAt(from);
        if (piece == null) {
            System.out.println("Клетка пустая");
        }

        if (piece.getColor() != currentPlayer) {
            System.out.println("Сейчас не ваш ход");
            return false;
        }

        boolean makeSuccessful = board.movePiece(from, to);
        if (makeSuccessful) {
            currentPlayer = currentPlayer.opposite();
            System.out.println("Ход выполнен. Теперь ходит: " + currentPlayer);
            return true;
        } else {
            System.out.println("Недопустимый ход");
            return false;
        }
    }

    public void promotePawn(Position pos, Class<? extends Piece> newPieceType) {
        Piece piece = board.getPieceAt(pos);

        if (piece == null) {
            throw new IllegalArgumentException("На позиции " + pos + " нет фигуры!");
        }

        if (!(piece instanceof Pawn)) {
            throw new IllegalArgumentException("На позиции " + pos + " нет пешки! Это " +
                    piece.getClass().getSimpleName());
        }

        if (newPieceType == Pawn.class) {
            throw new IllegalArgumentException("Пешка не может превратиться в пешку!");
        }
        if (newPieceType == King.class) {
            throw new IllegalArgumentException("Пешка не может превратиться в короля!");
        }

        Pawn pawn = (Pawn) piece;
        // Создаём новую фигуру того же цвета
        Piece newPiece = createPiece(newPieceType, pawn.getColor(), pos);

        // Заменяем пешку на новую фигуру
        board.replacePiece(pos, newPiece);

        System.out.println("Пешка превращена в " + newPiece.getClass().getSimpleName());
    }


    public boolean isPromotionPosition(Position pos, Color color) {
        // Белая пешка на 8-м ряду (row == 7)
        // Чёрная пешка на 1-м ряду (row == 0)
        int promotionRow = (color == Color.WHITE) ? 7 : 0;
        return pos.getRow() == promotionRow;
    }

    public List<String> getPromotionOptions() {
        List<String> options = Arrays.asList("Q", "R", "B", "N"); // Ферзь, Ладья, Слон, Конь

        if (gameType == GameType.OMEGA) {
            options.add("C"); // Champion
            options.add("W"); // Wizard
        }

        return options;
    }


    public Board getBoard() {
        return this.board;
    }

    public Color getCurrentPlayer() {
        return currentPlayer;
    }

    public GameType getGameType() {
        return this.gameType;
    }

    public boolean isGameOver() {
        return isGameOver;
    }

    public void surrender() {
        isGameOver = true;
        System.out.println((currentPlayer == Color.WHITE ? "Белые" : "Чёрные") + " сдались!");
    }

    public void capturePiece(Piece piece) {
        if (piece.getColor() == Color.WHITE) {
            capturedWhitePieces.add(piece);
            System.out.println("Снята белая фигура: " + piece.getClass().getSimpleName());
        } else {
            capturedBlackPieces.add(piece);
            System.out.println("Снята чёрная фигура: " + piece.getClass().getSimpleName());
        }
    }

    private Position enPassantTarget; // Позиция пешки, которую можно взять на проходе

    public Position getEnPassantTarget() {
        return enPassantTarget;
    }

    public void setEnPassantTarget(Position pos) {
        this.enPassantTarget = pos;
    }

    public void clearEnPassantTarget() {
        this.enPassantTarget = null;
    }


    public List<Piece> getCapturedWhitePieces() {
        return capturedWhitePieces;
    }

    public List<Piece> getCapturedBlackPieces() {
        return capturedBlackPieces;
    }
}
