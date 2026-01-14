package main;

import main.pieces.*;
import java.util.ArrayList;
import java.util.List;

public class MoveCalculator {
    // Убрали статическое поле BOARD_SIZE, будем получать размер доски из GameType

    private static boolean processMove(Piece piece, Board board,
                                       List<Position> moves, int row, int col) {
        Position pos = new Position(row, col);
        GameType gameType = board.getGame().getGameType();

        if (!pos.isValid(gameType)) {
            return false;
        }
        Piece target = board.getPieceAt(pos);

        if (target == null) {
            moves.add(pos);
            return true;
        } else if (piece.isOpponent(target)) {
            moves.add(pos);
            return false;
        } else {
            return false;
        }
    }

    public static List<Position> getRookMoves(Piece piece, Board board) {
        List<Position> moves = new ArrayList<>();
        int rowNow = piece.getPosition().getRow();
        int colNow = piece.getPosition().getCol();
        GameType gameType = board.getGame().getGameType();
        int boardSize = gameType.getBoardSize();

        // Вверх (+1, 0)
        for (int step = 1; rowNow + step < boardSize; step++) {
            if (!processMove(piece, board, moves, rowNow + step, colNow)) break;
        }
        // Вниз (-1, 0)
        for (int step = 1; rowNow - step >= 0; step++) {
            if (!processMove(piece, board, moves, rowNow - step, colNow)) break;
        }
        // Вправо (0, +1)
        for (int step = 1; colNow + step < boardSize; step++) {
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
        GameType gameType = board.getGame().getGameType();
        int boardSize = gameType.getBoardSize();

        // Вверх-вправо (+1, +1)
        for (int step = 1; rowNow + step < boardSize && colNow + step < boardSize; step++) {
            if (!processMove(piece, board, moves, rowNow + step, colNow + step)) break;
        }
        // Вверх-влево (+1, -1)
        for (int step = 1; rowNow + step < boardSize && colNow - step >= 0; step++) {
            if (!processMove(piece, board, moves, rowNow + step, colNow - step)) break;
        }
        // Вниз-вправо (-1, +1)
        for (int step = 1; rowNow - step >= 0 && colNow + step < boardSize; step++) {
            if (!processMove(piece, board, moves, rowNow - step, colNow + step)) break;
        }
        // Вниз-влево (-1, -1)
        for (int step = 1; rowNow - step >= 0 && colNow - step >= 0; step++) {
            if (!processMove(piece, board, moves, rowNow - step, colNow - step)) break;
        }

        return moves;
    }

    public static List<Position> getKingMoves(Piece piece, Board board) {
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
        int boardSize = gameType.getBoardSize();

        // ДВИЖЕНИЕ ВПЕРЁД ПЕШКИ БЕЗ ВЗЯТИЙ
        int offset_forward = pawn.getColor() == Color.WHITE ? +1 : -1;
        int X = 1; // Число клеток, на которое потенциально может сдвинуться вперёд пешка (pawn)
        if (!pawn.hasMoved()) {
            X = pawnInitialMaxSteps;
        }
        for (int i = 1; i <= X; i++) {
            Position forward_X = new Position(pawn.getPosition().getRow() + (i * offset_forward), pawn.getPosition().getCol());
            if (!forward_X.isValid(gameType)) {
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
        if (forwardRight.isValid(gameType)) {
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
        if (forwardLeft.isValid(gameType)) {
            Piece targetForwardLeft = board.getPieceAt(forwardLeft);
            if (pawn.isOpponent(targetForwardLeft)) {
                moves.add(forwardLeft);
            }
        }

        // ВЗЯТИЕ НА ПРОХОДЕ (только для классических шахмат 8x8)
        if (gameType == GameType.CLASSIC) {
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
        }

        return moves;
    }

    public static List<Position> getAllKingMoves(King king, Board board) {
        List<Position> allMoves = new ArrayList<>();

        // 1. Добавить обычные ходы
        allMoves.addAll(getKingMoves(king, board));

        // 2. Добавить рокировки (только для классических)
        if (board.getGame().getGameType() == GameType.CLASSIC) {
            List<CastlingInfo> possibleCastlings = getPossibleCastlings(king, board);
            for (CastlingInfo castling : possibleCastlings) {
                allMoves.add(castling.kingTo);
            }
        }

        return allMoves;
    }

    public static List<CastlingInfo> getPossibleCastlings(King king, Board board) {
        List<CastlingInfo> moves = new ArrayList<>();

        // Рокировки только для классических шахмат
        if (board.getGame().getGameType() != GameType.CLASSIC) {
            return moves;
        }

        // Получаем ВСЕ классические рокировки
        List<CastlingInfo> allClassic = CastlingInfo.getAllClassic();

        // Фильтруем по цвету короля и проверяем валидность
        for (CastlingInfo castling : allClassic) {
            if (castling.color == king.getColor() && castling.isValid(board)) {
                moves.add(castling);
            }
        }

        return moves;
    }

    public static List<Position> getChampionMoves(Piece piece, Board board) {
        List<Position> moves = new ArrayList<>();
        int rowNow = piece.getPosition().getRow();
        int colNow = piece.getPosition().getCol();
        GameType gameType = board.getGame().getGameType();

        // 1. Ходы как у коня
        int[][] knightOffsets = getKnightOffsets();
        for (int[] offset : knightOffsets) {
            int newRow = rowNow + offset[0];
            int newCol = colNow + offset[1];
            Position pos = new Position(newRow, newCol);

            if (pos.isValid(gameType)) {
                Piece target = board.getPieceAt(pos);
                if (target == null || piece.isOpponent(target)) {
                    moves.add(pos);
                }
            }
        }

        // 2. Ходы как у короля (на 1 клетку в любом направлении)
        int[][] kingOffsets = getKingOffsets();
        for (int[] offset : kingOffsets) {
            int newRow = rowNow + offset[0];
            int newCol = colNow + offset[1];
            Position pos = new Position(newRow, newCol);

            if (pos.isValid(gameType)) {
                Piece target = board.getPieceAt(pos);
                if (target == null || piece.isOpponent(target)) {
                    moves.add(pos);
                }
            }
        }

        return moves;
    }

    public static List<Position> getWizardMoves(Piece piece, Board board) {
        List<Position> moves = new ArrayList<>();
        int rowNow = piece.getPosition().getRow();
        int colNow = piece.getPosition().getCol();
        GameType gameType = board.getGame().getGameType();
        int boardSize = gameType.getBoardSize();

        // 1. Ходы как у слона (диагонали)
        // Вверх-вправо
        for (int step = 1; ; step++) {
            int newRow = rowNow + step;
            int newCol = colNow + step;
            Position pos = new Position(newRow, newCol);

            if (!pos.isValid(gameType)) break;
            Piece target = board.getPieceAt(pos);

            if (target == null) {
                moves.add(pos);
            } else if (piece.isOpponent(target)) {
                moves.add(pos);
                break;
            } else {
                break;
            }
        }

        // Вверх-влево
        for (int step = 1; ; step++) {
            int newRow = rowNow + step;
            int newCol = colNow - step;
            Position pos = new Position(newRow, newCol);

            if (!pos.isValid(gameType)) break;
            Piece target = board.getPieceAt(pos);

            if (target == null) {
                moves.add(pos);
            } else if (piece.isOpponent(target)) {
                moves.add(pos);
                break;
            } else {
                break;
            }
        }

        // Вниз-вправо
        for (int step = 1; ; step++) {
            int newRow = rowNow - step;
            int newCol = colNow + step;
            Position pos = new Position(newRow, newCol);

            if (!pos.isValid(gameType)) break;
            Piece target = board.getPieceAt(pos);

            if (target == null) {
                moves.add(pos);
            } else if (piece.isOpponent(target)) {
                moves.add(pos);
                break;
            } else {
                break;
            }
        }

        // Вниз-влево
        for (int step = 1; ; step++) {
            int newRow = rowNow - step;
            int newCol = colNow - step;
            Position pos = new Position(newRow, newCol);

            if (!pos.isValid(gameType)) break;
            Piece target = board.getPieceAt(pos);

            if (target == null) {
                moves.add(pos);
            } else if (piece.isOpponent(target)) {
                moves.add(pos);
                break;
            } else {
                break;
            }
        }

        // 2. Прыжки на 2 клетки (без перепрыгивания фигур)
        int[][] jumpOffsets = {
                {2, 0},  // Вверх 2
                {-2, 0}, // Вниз 2
                {0, 2},  // Вправо 2
                {0, -2}  // Влево 2
        };

        for (int[] offset : jumpOffsets) {
            int newRow = rowNow + offset[0];
            int newCol = colNow + offset[1];
            Position pos = new Position(newRow, newCol);

            if (pos.isValid(gameType)) {
                // Проверяем, что промежуточная клетка пуста
                int midRow = rowNow + offset[0]/2;
                int midCol = colNow + offset[1]/2;
                Position midPos = new Position(midRow, midCol);

                if (midPos.isValid(gameType) && board.getPieceAt(midPos) == null) {
                    Piece target = board.getPieceAt(pos);
                    if (target == null || piece.isOpponent(target)) {
                        moves.add(pos);
                    }
                }
            }
        }

        return moves;
    }
}