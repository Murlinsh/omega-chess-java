package main;

import main.pieces.*;

import java.util.List;
import java.util.Stack;

public class Board {
    private Piece[][] grid;
    private int BOARD_SIZE; // Убираем финальное, теперь зависит от типа игры
    private final Game game;
    private AttackMap whiteAttackMap;
    private AttackMap blackAttackMap;
    private Stack<MoveSnapshot> moveHistory = new Stack<>();

    public Board(Game game) {
        this.game = game;
        this.BOARD_SIZE = game.getGameType().getBoardSize(); // Получаем размер из GameType
        this.grid = new Piece[BOARD_SIZE][BOARD_SIZE];
        this.whiteAttackMap = new AttackMap();
        this.blackAttackMap = new AttackMap();
    }

    // Расстановка фигур на доску
    public void initializePieces() {
        GameType type = game.getGameType();
        if (type == GameType.CLASSIC) {
            setupClassicBoard();
        } else {
            setupOmegaBoard();
        }
    }

    private void setupClassicBoard() {
        // 1. Белые пешки (ряд 2 = row=1)
        for (int col = 0; col < BOARD_SIZE; col++) { // Используем BOARD_SIZE
            placePiece(new Pawn(Color.WHITE, new Position(1, col)));
        }

        // 2. Чёрные пешки (ряд 7 = row=6)
        for (int col = 0; col < BOARD_SIZE; col++) { // Используем BOARD_SIZE
            placePiece(new Pawn(Color.BLACK, new Position(BOARD_SIZE - 2, col))); // BOARD_SIZE - 2 вместо 6
        }

        // 3. Белые фигуры (ряд 1 = row=0)
        placePiece(new Rook(Color.WHITE, new Position(0, 0)));
        placePiece(new Knight(Color.WHITE, new Position(0, 1)));
        placePiece(new Bishop(Color.WHITE, new Position(0, 2)));
        placePiece(new Queen(Color.WHITE, new Position(0, 3)));
        placePiece(new King(Color.WHITE, new Position(0, 4)));
        placePiece(new Bishop(Color.WHITE, new Position(0, 5)));
        placePiece(new Knight(Color.WHITE, new Position(0, 6)));
        placePiece(new Rook(Color.WHITE, new Position(0, 7)));

        // 4. Чёрные фигуры (ряд 8 = row=7)
        placePiece(new Rook(Color.BLACK, new Position(BOARD_SIZE - 1, BOARD_SIZE - 1))); // (7,7) для 8x8
        placePiece(new Knight(Color.BLACK, new Position(BOARD_SIZE - 1, BOARD_SIZE - 2)));
        placePiece(new Bishop(Color.BLACK, new Position(BOARD_SIZE - 1, BOARD_SIZE - 3)));
        placePiece(new King(Color.BLACK, new Position(BOARD_SIZE - 1, BOARD_SIZE - 4)));
        placePiece(new Queen(Color.BLACK, new Position(BOARD_SIZE - 1, BOARD_SIZE - 5)));
        placePiece(new Bishop(Color.BLACK, new Position(BOARD_SIZE - 1, BOARD_SIZE - 6)));
        placePiece(new Knight(Color.BLACK, new Position(BOARD_SIZE - 1, BOARD_SIZE - 7)));
        placePiece(new Rook(Color.BLACK, new Position(BOARD_SIZE - 1, 0)));

        calculateAttackMaps();
    }

    private void setupOmegaBoard() {
        throw new UnsupportedOperationException("OMEGA chess not implemented yet");
    }

    public void calculateAttackMaps() {
        whiteAttackMap.clear();
        blackAttackMap.clear();

        for (int x = 0; x <= this.BOARD_SIZE - 1; x++) {
            for (int y = 0; y <= this.BOARD_SIZE - 1; y++) {
                Piece piece = grid[y][x];
                if (piece != null) {
                    Color pieceColor = piece.getColor();
                    List<Position> attackingSquares = piece.getAttackingSquares(this);
                    for (Position attackedSquare : attackingSquares) {
                        if (pieceColor == Color.WHITE) {
                            whiteAttackMap.addAttack(attackedSquare, piece);
                        } else {
                            blackAttackMap.addAttack(attackedSquare, piece);
                        }
                    }
                }
            }
        }
    }

    public boolean isSquareAttackedBy(Position square, Color attackerColor) {
        if (attackerColor == Color.WHITE) {
            return whiteAttackMap.isAttacked(square);
        } else {
            return blackAttackMap.isAttacked(square);
        }
    }

    public List<Piece> getAttackersOf(Position square, Color attackerColor) {
        if (attackerColor == Color.WHITE) {
            return whiteAttackMap.getAttackers(square);
        } else {
            return blackAttackMap.getAttackers(square);
        }
    }

    public boolean isKingInCheck(Color kingColor) {
        Position kingPos = findKingPosition(kingColor);
        if (kingPos == null) return false;

        Color opponentColor = (kingColor == Color.WHITE) ? Color.BLACK : Color.WHITE;
        return isSquareAttackedBy(kingPos, opponentColor);
    }

    private Position findKingPosition(Color kingColor) {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                Position pos = new Position(row, col);
                Piece piece = getPieceAt(pos);
                if (piece instanceof King && piece.getColor() == kingColor) {
                    return pos;
                }
            }
        }
        return null;
    }

    //    public boolean hasLegalMoves(Color color) {
//        for (int row = 0; row < BOARD_SIZE; row++) {
//            for (int col = 0; col < BOARD_SIZE; col++) {
//                Position from = new Position(row, col);
//                Piece piece = getPieceAt(from);
//
//                if (piece != null && piece.getColor() == color) {
//                    List<Position> moves = piece.getPossibleMoves(this);
//
//                    for (Position to : moves) {
//                        if (isMoveLegal(from, to, color)) {
//                            return true;
//                        }
//                    }
//                }
//            }
//        }
//        return false;
//    }
    public boolean hasLegalMoves(Color color) {
        System.out.println("\n=== Проверка hasLegalMoves для " + color + " ===");

        int piecesChecked = 0;
        int totalMoves = 0;

        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                Position from = new Position(row, col);
                Piece piece = getPieceAt(from);

                if (piece != null && piece.getColor() == color) {
                    piecesChecked++;
                    System.out.println("  Фигура " + piece.getClass().getSimpleName() +
                            " на " + from + " (" + row + "," + col + ")");

                    List<Position> moves = piece.getPossibleMoves(this);
                    System.out.println("    Возможных ходов: " + moves.size());
                    totalMoves += moves.size();

                    for (Position to : moves) {
                        boolean legal = isMoveLegal(from, to, color);
                        System.out.println("      " + from + " → " + to + " : " +
                                (legal ? "ЛЕГАЛЬНЫЙ" : "нелегальный"));
                        if (legal) {
                            System.out.println("=== Найден легальный ход, возвращаем true ===");
                            return true;
                        }
                    }
                }
            }
        }

        System.out.println("Итог: " + piecesChecked + " фигур, " +
                totalMoves + " возможных ходов, легальных: 0");
        System.out.println("=== Возвращаем false ===");
        return false;
    }


    public boolean isMoveLegal(Position from, Position to, Color movingColor) {
        // Базовые проверки
        Piece piece = getPieceAt(from);
        if (piece == null || piece.getColor() != movingColor) {
            return false;
        }

        if (!to.isValid(this.BOARD_SIZE)) {
            return false;
        }

        // Проверяем, является ли ход допустимым для фигуры
        if (!piece.getPossibleMoves(this).contains(to)) {
            return false;
        }

        // Проверяем, не съедает ли свою фигуру
        Piece target = getPieceAt(to);
        if (target != null && target.getColor() == movingColor) {
            return false;
        }

        // Проверяем, не оставляет ли ход короля под шахом
        return !wouldMoveLeaveKingInCheck(from, to, movingColor);
    }

    private boolean wouldMoveLeaveKingInCheck(Position from, Position to, Color movingColor) {
        // Сохраняем состояние
        Piece movedPiece = getPieceAt(from);
        Piece capturedPiece = getPieceAt(to);

        // Временный ход
        setPieceAtInternal(null, from);
        setPieceAtInternal(movedPiece, to);

        // Пересчитываем карты атак
        calculateAttackMaps();

        // Проверяем, не под шахом ли король
        boolean kingInCheck = isKingInCheck(movingColor);

        // Восстанавливаем состояние
        setPieceAtInternal(movedPiece, from);
        setPieceAtInternal(capturedPiece, to);
        calculateAttackMaps();

        return kingInCheck;
    }

    public boolean isCheckmate(Color color) {
        return isKingInCheck(color) && !hasLegalMoves(color);
    }

    public boolean isStalemate(Color color) {
        return !isKingInCheck(color) && !hasLegalMoves(color);
    }

    public Game getGame() {
        return game;
    }

    public Piece getPieceAt(Position position) {
        if (!position.isValid(this.BOARD_SIZE)) {
            return null;
        }
        return grid[position.getRow()][position.getCol()];
    }

    // Для начальной расстановки
    public void placePiece(Piece piece) {
        setPieceAtInternal(piece, piece.getPosition());
    }

    public boolean movePiece(Position from, Position to) {
        Piece piece = getPieceAt(from);
        if (piece == null) {
            return false;
        }

        if (!to.isValid(this.BOARD_SIZE)) {
            return false;
        }

        Color movingColor = piece.getColor();

        // ПРОВЕРКА РОКИРОВКИ (должна быть ДО снапшота для короля)
        if (piece instanceof King) {
            King king = (King) piece;
            List<CastlingInfo> possibleCastlings = MoveCalculator.getPossibleCastlings(king, this);

            for (CastlingInfo castling : possibleCastlings) {
                if (to.equals(castling.kingTo)) {
                    // Проверяем, что рокировка допустима
                    if (!isCastlingSafe(king.getColor(), castling)) {
                        return false;
                    }

                    // СОЗДАЁМ СНАПШОТ ПЕРЕД РОКИРОВКОЙ
                    MoveSnapshot snapshot = createSnapshotBeforeMove(from, to, piece);
                    moveHistory.push(snapshot);

                    // ВЫПОЛНЯЕМ РОКИРОВКУ
                    Piece rook = getPieceAt(castling.rookFrom);
                    if (!(rook instanceof Rook)) {
                        moveHistory.pop();
                        return false;
                    }

                    // Переместить короля
                    setPieceAtInternal(null, castling.kingFrom);
                    setPieceAtInternal(king, castling.kingTo);
                    king.markAsMoved();

                    // Переместить ладью
                    setPieceAtInternal(null, castling.rookFrom);
                    setPieceAtInternal(rook, castling.rookTo);
                    ((Rook) rook).markAsMoved();

                    // Обновить состояние
                    game.clearEnPassantTarget();
                    calculateAttackMaps();

                    return true;
                }
            }
        }

        // ДОБАВЛЕНО: Проверяем, разрешён ли ход для фигуры
        if (!piece.getPossibleMoves(this).contains(to)) {
            return false;
        }

        // ДОБАВЛЕНО: Проверяем, не оставляет ли ход короля под шахом
        if (wouldMoveLeaveKingInCheck(from, to, movingColor)) {
            return false;
        }

        // СОЗДАЁМ СНАПШОТ ПЕРЕД ОБЫЧНЫМ ХОДОМ
        MoveSnapshot snapshot = createSnapshotBeforeMove(from, to, piece);
        moveHistory.push(snapshot);

        // Отмечаем, что король и/или ладья ходили (для обычных ходов)
        if (piece instanceof King) {
            ((King) piece).markAsMoved();
        } else if (piece instanceof Rook) {
            ((Rook) piece).markAsMoved();
        }

        // Проверка взятия на проходе
        if (piece instanceof Pawn) {
            Position enPassantTarget = game.getEnPassantTarget();
            if (enPassantTarget != null && to.equals(enPassantTarget)) {
                // 1. ВЫЧИСЛИТЬ позицию взятой пешки
                int direction = (piece.getColor() == Color.WHITE) ? -1 : +1;
                int capturedRow = to.getRow() + direction;
                int capturedCol = to.getCol();
                Position capturedPos = new Position(capturedRow, capturedCol);

                // 2. НАЙТИ пешку на этой позиции
                Piece capturedPiece = getPieceAt(capturedPos);

                // 3. ЕСЛИ пешка существует:
                if (capturedPiece != null && capturedPiece instanceof Pawn) {
                    // - Добавить в capturedPieces
                    game.capturePiece(capturedPiece);
                    // - Удалить с доски
                    setPieceAtInternal(null, capturedPos);
                }

                // 4. ОЧИСТИТЬ enPassantTarget
                game.clearEnPassantTarget();
            }
        }

        // Добавляем фигуру оппонента с клетки to в список съеденных фигур (обычное взятие)
        Piece target = this.getPieceAt(to);
        if (target != null) {
            game.capturePiece(target);
        }

        // Очищаем старую клетку
        setPieceAtInternal(null, from);

        // Отмечаем пешку как moved и устанавливаем enPassantTarget при двойном ходе
        if (piece instanceof Pawn) {
            ((Pawn) piece).markAsMoved();

            // Проверка двойного хода для установки enPassantTarget
            int rowDiff = Math.abs(to.getRow() - from.getRow());
            int pawnInitialMaxSteps = game.getGameType().getPawnInitialMaxSteps();

            if (rowDiff == pawnInitialMaxSteps) { // 2 для CLASSIC, 3 для OMEGA
                // Устанавливаем позицию ЗА пешкой (поле, которое она проскочила)
                int direction = (piece.getColor() == Color.WHITE) ? -1 : 1;
                Position targetPos = new Position(to.getRow() + direction, to.getCol());
                game.setEnPassantTarget(targetPos);
            } else {
                // Если не двойной ход - очищаем enPassantTarget
                game.clearEnPassantTarget();
            }
        } else {
            // Если ход не пешкой - очищаем enPassantTarget
            game.clearEnPassantTarget();
        }

        // Ставим на новую клетку (затираем фигуру противника, если есть)
        setPieceAtInternal(piece, to);
        calculateAttackMaps();
        return true;
    }

    private void setPieceAtInternal(Piece piece, Position position) {
        if (piece != null) {
            piece.setPosition(position);
        }
        grid[position.getRow()][position.getCol()] = piece;
    }

    public static boolean isLightSquare(Position pos) {
        return (pos.getRow() + pos.getCol()) % 2 == 1;
    }

    public static boolean isDarkSquare(Position pos) {
        return !isLightSquare(pos);
    }

    public void replacePiece(Position pos, Piece newPiece) {
        if (pos.isValid(BOARD_SIZE)) {
            setPieceAtInternal(newPiece, pos);
        } else {
            throw new IllegalArgumentException("Некорректная позиция");
        }
    }

    private MoveSnapshot createSnapshotBeforeMove(Position from, Position to, Piece piece) {
        // Получаем фигуру на целевой клетке (если есть)
        Piece capturedPiece = getPieceAt(to);
        Position capturedPos = (capturedPiece != null) ? to : null;

        // Получаем информацию о рокировках
        King whiteKing = findKing(Color.WHITE);
        King blackKing = findKing(Color.BLACK);

        // Находим ладьи для рокировок
        Rook whiteRookKingSide = getRookAt(new Position(0, 7), Color.WHITE);
        Rook whiteRookQueenSide = getRookAt(new Position(0, 0), Color.WHITE);
        Rook blackRookKingSide = getRookAt(new Position(7, 7), Color.BLACK);
        Rook blackRookQueenSide = getRookAt(new Position(7, 0), Color.BLACK);

        // Проверяем, является ли ход рокировкой
        boolean isCastling = (piece instanceof King && Math.abs(from.getCol() - to.getCol()) == 2);

        // Проверяем, является ли ход взятием на проходе
        boolean isEnPassant = (piece instanceof Pawn && to.equals(game.getEnPassantTarget()));

        // Проверяем, является ли ход превращением пешки
        boolean isPromotion = (piece instanceof Pawn &&
                ((piece.getColor() == Color.WHITE && to.getRow() == 7) ||
                        (piece.getColor() == Color.BLACK && to.getRow() == 0)));

        Piece castlingRook = null;
        if (isCastling) {
            // Определяем, какая ладья участвует в рокировке
            if (to.getCol() == 6) { // Короткая рокировка
                castlingRook = getPieceAt(new Position(from.getRow(), 7));
            } else { // Длинная рокировка
                castlingRook = getPieceAt(new Position(from.getRow(), 0));
            }
        }

        return new MoveSnapshot(
                piece, from, to,
                capturedPiece, capturedPos,
                game.getEnPassantTarget(),
                getHasMovedState(piece),
                (capturedPiece != null) ? getHasMovedState(capturedPiece) : false,
                (whiteKing != null) ? whiteKing.hasMoved() : false,
                (blackKing != null) ? blackKing.hasMoved() : false,
                (whiteRookKingSide != null) ? whiteRookKingSide.hasMoved() : false,
                (whiteRookQueenSide != null) ? whiteRookQueenSide.hasMoved() : false,
                (blackRookKingSide != null) ? blackRookKingSide.hasMoved() : false,
                (blackRookQueenSide != null) ? blackRookQueenSide.hasMoved() : false,
                castlingRook,
                (castlingRook != null) ? getHasMovedState(castlingRook) : false,
                isCastling, isEnPassant, isPromotion
        );
    }

    private boolean getHasMovedState(Piece piece) {
        if (piece instanceof King) {
            return ((King) piece).hasMoved();
        } else if (piece instanceof Rook) {
            return ((Rook) piece).hasMoved();
        } else if (piece instanceof Pawn) {
            return ((Pawn) piece).hasMoved();
        }
        return false;
    }

    private void restoreFromSnapshot(MoveSnapshot snapshot) {
        // 1. Восстанавливаем перемещенную фигуру
        setPieceAtInternal(snapshot.movedPiece, snapshot.from);

        // 2. Восстанавливаем взятую фигуру (если была)
        if (snapshot.capturedPiece != null && snapshot.capturedPos != null) {
            setPieceAtInternal(snapshot.capturedPiece, snapshot.capturedPos);

            // Удаляем из списка съеденных фигур
            if (snapshot.capturedPiece.getColor() == Color.WHITE) {
                game.getCapturedWhitePieces().remove(snapshot.capturedPiece);
            } else {
                game.getCapturedBlackPieces().remove(snapshot.capturedPiece);
            }
        } else {
            // Очищаем целевую клетку
            setPieceAtInternal(null, snapshot.to);
        }

        // 3. Восстанавливаем состояние hasMoved
        restoreHasMovedState(snapshot.movedPiece, snapshot.movedPieceHasMovedBefore);
        if (snapshot.capturedPiece != null) {
            restoreHasMovedState(snapshot.capturedPiece, snapshot.capturedPieceHasMovedBefore);
        }

        // 4. Восстанавливаем состояние рокировок
        restoreCastlingStates(snapshot);

        // 5. Восстанавливаем enPassantTarget
        if (snapshot.enPassantTargetBefore != null) {
            game.setEnPassantTarget(snapshot.enPassantTargetBefore);
        } else {
            game.clearEnPassantTarget();
        }

        // 6. Особый случай: рокировка
        if (snapshot.isCastling && snapshot.castlingRook != null) {
            // Определяем, откуда перемещалась ладья
            Position rookFrom;
            Position rookTo;

            if (snapshot.to.getCol() == 6) { // Короткая рокировка
                rookFrom = new Position(snapshot.from.getRow(), 7);
                rookTo = new Position(snapshot.from.getRow(), 5);
            } else { // Длинная рокировка
                rookFrom = new Position(snapshot.from.getRow(), 0);
                rookTo = new Position(snapshot.from.getRow(), 3);
            }

            // Возвращаем ладью на место
            setPieceAtInternal(snapshot.castlingRook, rookFrom);
            setPieceAtInternal(null, rookTo);
            restoreHasMovedState(snapshot.castlingRook, snapshot.castlingRookHasMovedBefore);
        }

        // 7. Пересчитываем карты атак
        calculateAttackMaps();
    }

    private void restoreHasMovedState(Piece piece, boolean hasMovedBefore) {
        if (piece instanceof King) {
            King king = (King) piece;
            if (hasMovedBefore) {
                king.markAsMoved();
            } else {
                king.resetMoved();
            }
        } else if (piece instanceof Rook) {
            Rook rook = (Rook) piece;
            if (hasMovedBefore) {
                rook.markAsMoved();
            } else {
                rook.resetMoved();
            }
        } else if (piece instanceof Pawn) {
            Pawn pawn = (Pawn) piece;
            if (hasMovedBefore) {
                pawn.markAsMoved();
            } else {
                pawn.resetMoved();
            }
        }
    }

    private void restoreCastlingStates(MoveSnapshot snapshot) {
        // Восстанавливаем состояние королей
        King whiteKing = findKing(Color.WHITE);
        King blackKing = findKing(Color.BLACK);

        if (whiteKing != null) {
            if (snapshot.whiteKingHasMovedBefore) {
                whiteKing.markAsMoved();
            } else {
                whiteKing.resetMoved();
            }
        }

        if (blackKing != null) {
            if (snapshot.blackKingHasMovedBefore) {
                blackKing.markAsMoved();
            } else {
                blackKing.resetMoved();
            }
        }

        // Восстанавливаем состояние ладей
        restoreRookState(new Position(0, 7), snapshot.whiteRookKingSideHasMovedBefore, Color.WHITE);
        restoreRookState(new Position(0, 0), snapshot.whiteRookQueenSideHasMovedBefore, Color.WHITE);
        restoreRookState(new Position(7, 7), snapshot.blackRookKingSideHasMovedBefore, Color.BLACK);
        restoreRookState(new Position(7, 0), snapshot.blackRookQueenSideHasMovedBefore, Color.BLACK);
    }

    private void restoreRookState(Position pos, boolean hasMovedBefore, Color color) {
        Piece piece = getPieceAt(pos);
        if (piece instanceof Rook && piece.getColor() == color) {
            Rook rook = (Rook) piece;
            if (hasMovedBefore) {
                rook.markAsMoved();
            } else {
                rook.resetMoved();
            }
        }
    }

    private King findKing(Color color) {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                Piece piece = grid[row][col];
                if (piece instanceof King && piece.getColor() == color) {
                    return (King) piece;
                }
            }
        }
        return null;
    }

    private Rook getRookAt(Position pos, Color color) {
        Piece piece = getPieceAt(pos);
        if (piece instanceof Rook && piece.getColor() == color) {
            return (Rook) piece;
        }
        return null;
    }

    public boolean undoLastMove() {
        if (moveHistory.isEmpty()) {
            return false;
        }

        MoveSnapshot snapshot = moveHistory.pop();
        restoreFromSnapshot(snapshot);
        return true;
    }

    /**
     * Проверяет безопасность рокировки
     */
    private boolean isCastlingSafe(Color color, CastlingInfo castling) {
        // Король не должен быть под шахом
        if (isKingInCheck(color)) {
            return false;
        }

        // Клетки, через которые проходит король, не должны быть атакованы
        Color opponentColor = (color == Color.WHITE) ? Color.BLACK : Color.WHITE;

        // Проверяем клетки от начальной до конечной позиции короля
        int row = castling.kingFrom.getRow();
        int startCol = Math.min(castling.kingFrom.getCol(), castling.kingTo.getCol());
        int endCol = Math.max(castling.kingFrom.getCol(), castling.kingTo.getCol());

        for (int col = startCol; col <= endCol; col++) {
            Position pos = new Position(row, col);
            if (isSquareAttackedBy(pos, opponentColor)) {
                return false;
            }
        }

        return true;
    }
}