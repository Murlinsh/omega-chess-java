package main;

import main.pieces.King;
import main.pieces.Piece;
import main.pieces.Rook;

import java.util.ArrayList;
import java.util.List;

public class CastlingInfo {
    public final Position kingFrom;
    public final Position kingTo;
    public final Position rookFrom;
    public final Position rookTo;
    public final Color color;
    public final boolean isShort;

    // Конструктор
    public CastlingInfo(Position kingFrom, Position kingTo,
                        Position rookFrom, Position rookTo,
                        Color color, boolean isShort) {
        this.kingFrom = kingFrom;
        this.kingTo = kingTo;
        this.rookFrom = rookFrom;
        this.rookTo = rookTo;
        this.color = color;
        this.isShort = isShort;
    }

    // === ФАБРИЧНЫЕ МЕТОДЫ ДЛЯ КЛАССИЧЕСКИХ ШАХМАТ ===

    // Белые, короткая рокировка (O-O)
    public static CastlingInfo classicWhiteShort() {
        return new CastlingInfo(
                new Position(0, 4), // e1
                new Position(0, 6), // g1
                new Position(0, 7), // h1
                new Position(0, 5), // f1
                Color.WHITE,
                true
        );
    }

    // Белые, длинная рокировка (O-O-O)
    public static CastlingInfo classicWhiteLong() {
        return new CastlingInfo(
                new Position(0, 4), // e1
                new Position(0, 2), // c1
                new Position(0, 0), // a1
                new Position(0, 3), // d1
                Color.WHITE,
                false
        );
    }

    // Чёрные, короткая рокировка (O-O)
    public static CastlingInfo classicBlackShort() {
        return new CastlingInfo(
                new Position(7, 4), // e8
                new Position(7, 6), // g8
                new Position(7, 7), // h8
                new Position(7, 5), // f8
                Color.BLACK,
                true
        );
    }

    // Чёрные, длинная рокировка (O-O-O)
    public static CastlingInfo classicBlackLong() {
        return new CastlingInfo(
                new Position(7, 4), // e8
                new Position(7, 2), // c8
                new Position(7, 0), // a8
                new Position(7, 3), // d8
                Color.BLACK,
                false
        );
    }

    // === ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ===

    // Получить клетки между королём и ладьёй (проверка на пустоту)
    public List<Position> getSquaresBetweenKingAndRook() {
        List<Position> squares = new ArrayList<>();
        int startCol = Math.min(kingFrom.getCol(), rookFrom.getCol()) + 1;
        int endCol = Math.max(kingFrom.getCol(), rookFrom.getCol()) - 1;
        int row = kingFrom.getRow(); // та же строка

        for (int col = startCol; col <= endCol; col++) {
            squares.add(new Position(row, col));
        }
        return squares;
    }

    // Получить клетки, через которые проходит король (включая начальную!)
    public List<Position> getKingPathSquares() {
        List<Position> path = new ArrayList<>();
        int row = kingFrom.getRow();
        int startCol = kingFrom.getCol();
        int endCol = kingTo.getCol();

        // Определяем направление
        int step = (endCol > startCol) ? 1 : -1;

        // Добавляем ВСЕ клетки, включая начальную и конечную
        for (int col = startCol; col != endCol + step; col += step) {
            path.add(new Position(row, col));
        }

        return path;
    }

    // Получить все 4 классических варианта
    public static List<CastlingInfo> getAllClassic() {
        List<CastlingInfo> all = new ArrayList<>();
        all.add(classicWhiteShort());
        all.add(classicWhiteLong());
        all.add(classicBlackShort());
        all.add(classicBlackLong());
        return all;
    }

    // === МЕТОД ПРОВЕРКИ ===

    public boolean isValid(Board board) {
        // 1. Проверка, что король на месте и не двигался
        Piece king = board.getPieceAt(kingFrom);
        if (!(king instanceof King) || ((King) king).hasMoved()) {
            return false;
        }

        // 2. Проверка, что ладья на месте и не двигалась
        Piece rook = board.getPieceAt(rookFrom);
        if (!(rook instanceof Rook) || ((Rook) rook).hasMoved()) {
            return false;
        }

        // 3. Проверка, что клетки между королём и ладьёй пусты
        for (Position square : getSquaresBetweenKingAndRook()) {
            if (board.getPieceAt(square) != null) {
                return false;
            }
        }

        // 4. Проверка, что король не под шахом
        if (board.isKingInCheck(color)) {
            return false;
        }

        // 5. Проверка, что клетки, через которые проходит король, не атакованы
        Color opponentColor = (color == Color.WHITE) ? Color.BLACK : Color.WHITE;
        for (Position square : getKingPathSquares()) {
            if (board.isSquareAttackedBy(square, opponentColor)) {
                return false;
            }
        }

        return true;
    }

    // Для отладки
    @Override
    public String toString() {
        return (color == Color.WHITE ? "Белые" : "Чёрные") +
                (isShort ? " O-O" : " O-O-O") +
                " (Король: " + kingFrom + "→" + kingTo +
                ", Ладья: " + rookFrom + "→" + rookTo + ")";
    }
}