package main;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MoveCalculator {
    private static final int BOARD_SIZE = 8;

    private static boolean processMove(Piece piece, Board board,
                                       List<Position> moves, int row, int col) {
        Position pos = new Position(row, col);
        if (!pos.isValid(BOARD_SIZE)) {
            return false; // Некорректная позиция
        }
        Piece target = board.getPieceAt(pos);

        if (target == null) {
            moves.add(pos);
            return true; // Продолжаем цикл
        } else if (piece.isOpponent(target)) {
            moves.add(pos);
            return false; // Прерываем цикл (взяли фигуру оппонента)
        } else {
            return false; // Прерываем цикл (своя фигура)
        }
    }

    public static List<Position> getRookMoves(Piece piece, Board board) {
        List<Position> moves = new ArrayList<>();
        int rowNow = piece.getPosition().getRow();
        int colNow = piece.getPosition().getCol();

        // Вверх (+1, 0)
        for (int step = 1; rowNow + step < BOARD_SIZE; step++) {
            if (!processMove(piece, board, moves, rowNow + step, colNow)) break;
        }
        // Вниз (-1, 0)
        for (int step = 1; rowNow - step >= 0; step++) {
            if (!processMove(piece, board, moves, rowNow - step, colNow)) break;
        }
        // Вправо (0, +1)
        for (int step = 1; colNow + step < BOARD_SIZE; step++) {
            if (!processMove(piece, board, moves, rowNow, colNow + step)) break;
        }
        // Влево (0, -1)
        for (int step = 1; colNow - step >= 0; step++) {
            if (!processMove(piece, board, moves, rowNow, colNow - step)) break;
        }

        return moves;
    }

    public static List<Position> getBishopMoves(Piece piece, Board board) {
        List<Position> moves = new ArrayList<>();
        int rowNow = piece.getPosition().getRow();
        int colNow = piece.getPosition().getCol();

        // Вверх-вправо (+1, +1)
        for (int step = 1; rowNow + step < BOARD_SIZE && colNow + step < BOARD_SIZE; step++) {
            if (!processMove(piece, board, moves, rowNow + step, colNow + step)) break;
        }
        // Вверх-влево (+1, -1)
        for (int step = 1; rowNow + step < BOARD_SIZE && colNow - step >= 0; step++) {
            if (!processMove(piece, board, moves, rowNow + step, colNow - step)) break;
        }
        // Вниз-вправо (-1, +1)
        for (int step = 1; rowNow - step >= 0 && colNow + step < BOARD_SIZE; step++) {
            if (!processMove(piece, board, moves, rowNow - step, colNow + step)) break;
        }
        // Вниз-влево (-1, -1)
        for (int step = 1; rowNow - step >= 0 && colNow - step >= 0; step++) {
            if (!processMove(piece, board, moves, rowNow - step, colNow - step)) break;
        }

        return moves;
    }

    public static List<Position> getKingMoves(Piece piece, Board board) {
        // Использует getKingOffsets() и processMove
        List<Position> moves = new ArrayList<>();
        int rowNow = piece.getPosition().getRow();
        int colNow = piece.getPosition().getCol();

        for (int[] offset : getKingOffsets()) {
            int deltaRow = offset[0];
            int deltaCol = offset[1];
            processMove(piece, board, moves, rowNow + deltaRow, colNow + deltaCol);
        }
        return moves;
    }

    private static int[][] getKingOffsets() {
        return new int[][]{
                {-1, -1}, {-1, 0}, {-1, 1},
                {0, -1}, {0, 1},
                {1, -1}, {1, 0}, {1, 1}
        };
    }

    public static List<Position> getKnightMoves(Piece piece, Board board) {
        // Использует getKnightOffsets() и processMove
        List<Position> moves = new ArrayList<>();
        int rowNow = piece.getPosition().getRow();
        int colNow = piece.getPosition().getCol();

        for (int[] offset : getKnightOffsets()) {
            int deltaRow = offset[0];
            int deltaCol = offset[1];
            processMove(piece, board, moves, rowNow + deltaRow, colNow + deltaCol);
        }
        return moves;
    }

    private static int[][] getKnightOffsets() {
        return new int[][]{
                {-2, -1}, {-2, 1}, {-1, -2}, {-1, 2},
                {1, -2}, {1, 2}, {2, -1}, {2, 1}
        };
    }

    public static List<Position> getPawnMoves(Pawn pawn, Board board) {
        List<Position> moves = new ArrayList<>();
        GameType gameType = board.getGame().getGameType();
        int pawnInitialMaxSteps = gameType.getPawnInitialMaxSteps();

        // ДВИЖЕНИЕ ВПЕРЁД ПЕШКИ БЕЗ ВЗЯТИЙ
        int offset_forward = pawn.getColor() == Color.WHITE ? +1 : -1;
        int X = 1; // Число клеток, на которое потенциально может сдвинуться вперёд пешка (pawn)
        if (!pawn.hasMoved()) {
            X = pawnInitialMaxSteps;
        }
        for (int i = 1; i <= X; i++) {
            Position forward_X = new Position(pawn.getPosition().getRow() + (i * offset_forward), pawn.getPosition().getCol());
            if (!forward_X.isValid(BOARD_SIZE)) {
                //changePawnToOther();
                break; // Некорректная позиция
            }
            Piece target = board.getPieceAt(forward_X);

            if (target == null) {
                moves.add(forward_X);
            } else {
                break;
            }
        }

        // ДВИЖЕНИЕ ПЕШКИ ВПЕРЁД ПО ДИАГОНАЛИ ПРИ ВЗЯТИИ ФИГУРЫ ОППОНЕНТА

        // Вперёд-вправо
        Position forwardRight = new Position(
                pawn.getPosition().getRow() + offset_forward,
                pawn.getPosition().getCol() + 1
        );
        if (forwardRight.isValid(BOARD_SIZE)) {
            Piece targetForwardRight = board.getPieceAt(forwardRight);
            if (pawn.isOpponent(targetForwardRight)) {
                moves.add(forwardRight);
            }
        }
        // Вперёд-влево
        Position forwardLeft = new Position(
                pawn.getPosition().getRow() + offset_forward,
                pawn.getPosition().getCol() - 1
        );
        if (forwardLeft.isValid(BOARD_SIZE)) {
            Piece targetForwardLeft = board.getPieceAt(forwardLeft);
            if (pawn.isOpponent(targetForwardLeft)) {
                moves.add(forwardLeft);
            }
        }

        // ВЗЯТИЕ НА ПРОХОДЕ
        if ((pawn.getColor() == Color.WHITE && pawn.getPosition().getRow() == 4)
                || (pawn.getColor() == Color.BLACK && pawn.getPosition().getRow() == 3)) {

            Position enPassantTarget = board.getGame().getEnPassantTarget();
            if (enPassantTarget != null) {
                // Проверка, что цель на проходе на соседней вертикали
                int pawnCol = pawn.getPosition().getCol();
                int targetCol = enPassantTarget.getCol();

                if (Math.abs(pawnCol - targetCol) == 1) {  // Соседняя вертикаль
                    moves.add(enPassantTarget);
                }
            }
        }

        return moves;
    }
}