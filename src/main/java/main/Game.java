package main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Game {
    private GameType gameType;
    private Board board;
    private Color currentPlayer;
    private boolean isGameOver;
    private int moveCount;

    private List<Piece> capturedWhitePieces = new ArrayList<>();
    private List<Piece> capturedBlackPieces = new ArrayList<>();
    private Position enPassantTarget; // Позиция пешки, которую можно взять на проходе

    public Game(GameType gameType) {
        this.gameType = gameType;
        this.board = new Board(this);
        this.currentPlayer = Color.WHITE;
        this.enPassantTarget = null;
        this.moveCount = 0;
        this.board.initializePieces();
    }

    private Piece createPiece(Class<? extends Piece> pieceType, Color color, Position pos) {
        if (pieceType == Queen.class) return new Queen(color, pos);
        if (pieceType == Rook.class) return new Rook(color, pos);
        if (pieceType == Bishop.class) return new Bishop(color, pos);
        if (pieceType == Knight.class) return new Knight(color, pos);
        if (pieceType == King.class) return new King(color, pos);
        if (pieceType == Pawn.class) return new Pawn(color, pos);

        // Для OMEGA-шахмат:
        if (gameType == GameType.OMEGA) {
            // if (pieceType == Champion.class) return new Champion(color, pos);
            // if (pieceType == Wizard.class) return new Wizard(color, pos);
        }

        throw new IllegalArgumentException("Unknown piece type: " + pieceType);
    }

    public boolean makeMove(Position from, Position to) {
        if (isGameOver()) {
            return false;
        }

        Piece piece = board.getPieceAt(from);
        if (piece == null) {
            return false;
        }

        if (piece.getColor() != currentPlayer) {
            return false;
        }

        boolean makeSuccessful = board.movePiece(from, to);
        if (makeSuccessful) {
            moveCount++;

            // Меняем игрока
            currentPlayer = currentPlayer.opposite();

            // Только шах можно проверить (для логирования)
            if (board.isKingInCheck(currentPlayer)) {
                System.out.println("ШАХ королю " + currentPlayer);
            }

            return true;
        } else {
            return false;
        }
    }


    public boolean undoLastMove() {
        if (isGameOver) {
            System.out.println("Игра завершена, нельзя отменить ход");
            return false;
        }

        boolean success = board.undoLastMove();
        if (success) {
            moveCount = Math.max(0, moveCount - 1); // Уменьшаем счетчик ходов
            // Сменить игрока обратно
            currentPlayer = currentPlayer.opposite();
            System.out.println("Ход отменен. Теперь ходит: " + currentPlayer);
            return true;
        } else {
            System.out.println("Нельзя отменить ход (история пуста)");
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

    public void setGameOver(boolean gameOver) {
        this.isGameOver = gameOver;
    }

    public void declareMate(Color winner) {
        this.isGameOver = true;
        System.out.println("МАТ! Победили " + (winner == Color.WHITE ? "белые" : "черные"));
    }

    public void declareStalemate() {
        this.isGameOver = true;
        System.out.println("ПАТ! Ничья");
    }

    // Метод для получения текущего состояния игры (для сохранения)
    public GameState getCurrentState() {
        // TODO: Реализовать создание GameState
        return null;
    }

    // Метод для восстановления состояния (для загрузки)
    public void restoreState(GameState state) {
        // TODO: Реализовать восстановление из GameState
    }

    // Метод для проверки, находится ли игра в дебюте, миттельшпиле или эндшпиле
    public String getGamePhase() {
        int totalPieces = 0;
        int queens = 0;
        int rooks = 0;
        int bishops = 0;
        int knights = 0;

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = board.getPieceAt(new Position(row, col));
                if (piece != null) {
                    totalPieces++;
                    if (piece instanceof Queen) queens++;
                    if (piece instanceof Rook) rooks++;
                    if (piece instanceof Bishop) bishops++;
                    if (piece instanceof Knight) knights++;
                }
            }
        }

        // Простая логика определения фазы игры
        if (totalPieces > 20) {
            return "Дебют";
        } else if (totalPieces > 10) {
            return "Миттельшпиль";
        } else {
            return "Эндшпиль";
        }
    }

    // Метод для получения количества ходов в игре
    public int getMoveCount() {
        return this.moveCount;
    }

    // Метод для проверки возможности рокировки
    public boolean canCastle(Color color, boolean kingSide) {
        King king = findKing(color);
        if (king == null || king.hasMoved()) {
            return false;
        }

        Position rookPos = kingSide ?
                (color == Color.WHITE ? new Position(0, 7) : new Position(7, 7)) :
                (color == Color.WHITE ? new Position(0, 0) : new Position(7, 0));

        Piece rook = board.getPieceAt(rookPos);
        if (!(rook instanceof Rook) || ((Rook) rook).hasMoved()) {
            return false;
        }

        return true;
    }

    private King findKing(Color color) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = board.getPieceAt(new Position(row, col));
                if (piece instanceof King && piece.getColor() == color) {
                    return (King) piece;
                }
            }
        }
        return null;
    }

    // Метод для получения оценки позиции (очень упрощенная)
    public int evaluatePosition() {
        int score = 0;

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = board.getPieceAt(new Position(row, col));
                if (piece != null) {
                    int pieceValue = getPieceValue(piece);
                    if (piece.getColor() == Color.WHITE) {
                        score += pieceValue;
                    } else {
                        score -= pieceValue;
                    }
                }
            }
        }

        return score;
    }

    private int getPieceValue(Piece piece) {
        if (piece instanceof Pawn) return 1;
        if (piece instanceof Knight) return 3;
        if (piece instanceof Bishop) return 3;
        if (piece instanceof Rook) return 5;
        if (piece instanceof Queen) return 9;
        if (piece instanceof King) return 100;
        return 0;
    }

    // Метод для отображения статистики игры
    public void displayStatistics() {
        System.out.println("=== СТАТИСТИКА ИГРЫ ===");
        System.out.println("Фаза игры: " + getGamePhase());
        System.out.println("Счет материала: " + evaluatePosition() + " (положительное - преимущество белых)");
        System.out.println("Снятые белые фигуры: " + capturedWhitePieces.size());
        System.out.println("Снятые черные фигуры: " + capturedBlackPieces.size());
        System.out.println("Количество ходов: " + getMoveCount()); // ← ЭТУ СТРОКУ ДОБАВЬ
        System.out.println("Можно ли белым сделать рокировку O-O: " + canCastle(Color.WHITE, true));
        System.out.println("Можно ли белым сделать рокировку O-O-O: " + canCastle(Color.WHITE, false));
        System.out.println("Можно ли черным сделать рокировку O-O: " + canCastle(Color.BLACK, true));
        System.out.println("Можно ли черным сделать рокировку O-O-O: " + canCastle(Color.BLACK, false));
        System.out.println("Поле для взятия на проходе: " +
                (enPassantTarget != null ? enPassantTarget : "нет"));
        System.out.println("=====================");
    }
}