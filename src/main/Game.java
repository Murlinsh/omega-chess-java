import java.util.ArrayList;
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

    public List<Piece> getCapturedWhitePieces() {
        return capturedWhitePieces;
    }

    public List<Piece> getCapturedBlackPieces() {
        return capturedBlackPieces;
    }
}
