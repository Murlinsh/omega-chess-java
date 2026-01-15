package main;

import main.pieces.*;

import java.util.List;
import java.util.Stack;

public class Board {
    private Piece[][] mainGrid;  // Основная доска
    private Piece[][] cornerGrid; // Угловые клетки (только для OMEGA)
    private int BOARD_SIZE; // Размер основной доски
    private final Game game;
    private AttackMap whiteAttackMap;
    private AttackMap blackAttackMap;
    private Stack<MoveSnapshot> moveHistory = new Stack<>();

    public Board(Game game) {
        this.game = game;
        GameType gameType = game.getGameType();

        if (gameType == GameType.CLASSIC) {
            this.BOARD_SIZE = 8;
            this.mainGrid = new Piece[BOARD_SIZE][BOARD_SIZE];
            this.cornerGrid = null; // Классика не использует углы
        } else {
            this.BOARD_SIZE = 10;
            this.mainGrid = new Piece[BOARD_SIZE][BOARD_SIZE];
            this.cornerGrid = new Piece[2][2]; // [0][0] - Белый Чемпион, [0][1] - Белый Волшебник
            // [1][0] - Черный Чемпион, [1][1] - Черный Волшебник
        }

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
        for (int col = 0; col < BOARD_SIZE; col++) {
            placePiece(new Pawn(Color.WHITE, new Position(1, col)));
        }

        // 2. Чёрные пешки (ряд 7 = row=6)
        for (int col = 0; col < BOARD_SIZE; col++) {
            placePiece(new Pawn(Color.BLACK, new Position(BOARD_SIZE - 2, col)));
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
        placePiece(new Rook(Color.BLACK, new Position(BOARD_SIZE - 1, BOARD_SIZE - 1)));
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
        // Очищаем основную доску
        this.mainGrid = new Piece[BOARD_SIZE][BOARD_SIZE];

        // Угловые клетки остаются как есть (cornerGrid[2][2])
        if (cornerGrid != null) {
            this.cornerGrid = new Piece[2][2];
        }

        // 1. БЕЛЫЕ ПЕШКИ (ряд 2 = row=1) - ВСЕ 10 пешек!
        for (int col = 0; col < 10; col++) {
            placePiece(new Pawn(Color.WHITE, new Position(1, col)));
        }

        // 2. БЕЛЫЕ ФИГУРЫ (ряд 1 = row=0) - правильная Omega Chess расстановка
        // a1, b1, c1, d1, e1, f1, g1, h1, i1, j1
        placePiece(new Champion(Color.WHITE, new Position(0, 0)));   // a1
        placePiece(new Rook(Color.WHITE, new Position(0, 1)));       // b1
        placePiece(new Knight(Color.WHITE, new Position(0, 2)));     // c1
        placePiece(new Bishop(Color.WHITE, new Position(0, 3)));     // d1
        placePiece(new Queen(Color.WHITE, new Position(0, 4)));      // e1
        placePiece(new King(Color.WHITE, new Position(0, 5)));       // f1
        placePiece(new Bishop(Color.WHITE, new Position(0, 6)));     // g1
        placePiece(new Knight(Color.WHITE, new Position(0, 7)));     // h1
        placePiece(new Rook(Color.WHITE, new Position(0, 8)));       // i1
        placePiece(new Champion(Color.WHITE, new Position(0, 9)));   // j1

        // 3. ЧЕРНЫЕ ПЕШКИ (ряд 9 = row=8) - ВСЕ 10 пешек!
        for (int col = 0; col < 10; col++) {
            placePiece(new Pawn(Color.BLACK, new Position(8, col)));
        }

        // 4. ЧЕРНЫЕ ФИГУРЫ (ряд 10 = row=9)
        // a10, b10, c10, d10, e10, f10, g10, h10, i10, j10
        placePiece(new Champion(Color.BLACK, new Position(9, 0)));   // a10
        placePiece(new Rook(Color.BLACK, new Position(9, 1)));       // b10
        placePiece(new Knight(Color.BLACK, new Position(9, 2)));     // c10
        placePiece(new Bishop(Color.BLACK, new Position(9, 3)));     // d10
        placePiece(new Queen(Color.BLACK, new Position(9, 4)));      // e10
        placePiece(new King(Color.BLACK, new Position(9, 5)));       // f10
        placePiece(new Bishop(Color.BLACK, new Position(9, 6)));     // g10
        placePiece(new Knight(Color.BLACK, new Position(9, 7)));     // h10
        placePiece(new Rook(Color.BLACK, new Position(9, 8)));       // i10
        placePiece(new Champion(Color.BLACK, new Position(9, 9)));   // j10

        // 5. УГЛОВЫЕ ВОЛШЕБНИКИ (cornerGrid)
        if (cornerGrid != null) {
            // БЕЛЫЕ волшебники в верхних углах (ряд -1)
            cornerGrid[0][0] = new Wizard(Color.WHITE, new Position(-1, -1, true)); // w1 - Белый
            cornerGrid[0][1] = new Wizard(Color.WHITE, new Position(-1, 10, true)); // w2 - Белый
            // ЧЕРНЫЕ волшебники в нижних углах (ряд 10)
            cornerGrid[1][0] = new Wizard(Color.BLACK, new Position(10, -1, true)); // w3 - Черный
            cornerGrid[1][1] = new Wizard(Color.BLACK, new Position(10, 10, true)); // w4 - ЧерныйcornerGrid[1][1] = new Wizard(Color.WHITE, new Position(10, 10, true)); // w4
        }

        calculateAttackMaps();
    }


    public void debugPrintOmegaSetup() {
        System.out.println("\n=== ПРОВЕРКА РАССТАНОВКИ OMEGA CHESS ===");

        // Проверяем ВСЕ угловые фигуры
        System.out.println("Угловые фигуры:");
        Position[] cornerPositions = {
                new Position(-1, -1, true),  // w1
                new Position(-1, 10, true),  // w2
                new Position(10, -1, true),  // w3
                new Position(10, 10, true)   // w4
        };

        for (Position pos : cornerPositions) {
            Piece piece = getPieceAt(pos);
            System.out.println(pos + ": " +
                    (piece != null ?
                            piece.getClass().getSimpleName() + " " + piece.getColor() :
                            "null"));
        }

        // Проверяем крайние фигуры на основной доске
        System.out.println("\nКрайние фигуры на основной доске:");
        System.out.println("a1 (0,0): " +
                (getPieceAt(new Position(0, 0)) != null ?
                        getPieceAt(new Position(0, 0)).getClass().getSimpleName() + " " + getPieceAt(new Position(0, 0)).getColor() : "null"));
        System.out.println("j1 (0,9): " +
                (getPieceAt(new Position(0, 9)) != null ?
                        getPieceAt(new Position(0, 9)).getClass().getSimpleName() + " " + getPieceAt(new Position(0, 9)).getColor() : "null"));
    }

    public void calculateAttackMaps() {
        whiteAttackMap.clear();
        blackAttackMap.clear();

        // Сканируем основную доску
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                Piece piece = mainGrid[row][col];
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

        // Сканируем угловые клетки (только для OMEGA)
        if (game.getGameType() == GameType.OMEGA && cornerGrid != null) {
            // Проверяем все 4 угловые клетки
            Position[] cornerPositions = {
                    new Position(-1, -1, true),  // Белый Чемпион
                    new Position(-1, 10, true),  // Белый Волшебник
                    new Position(10, -1, true),  // Черный Чемпион
                    new Position(10, 10, true)   // Черный Волшебник
            };

            for (Position cornerPos : cornerPositions) {
                Piece piece = getPieceAt(cornerPos);
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
                Piece piece = mainGrid[row][col];
                if (piece instanceof King && piece.getColor() == kingColor) {
                    return new Position(row, col);
                }
            }
        }
        return null;
    }

    public boolean hasLegalMoves(Color color) {
        System.out.println("\n=== Проверка hasLegalMoves для " + color + " ===");

        int piecesChecked = 0;
        int totalMoves = 0;

        // Проверяем фигуры на основной доске
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

        // Проверяем угловые фигуры (только для OMEGA)
        if (game.getGameType() == GameType.OMEGA) {
            Position[] cornerPositions = {
                    new Position(-1, -1, true),
                    new Position(-1, 10, true),
                    new Position(10, -1, true),
                    new Position(10, 10, true)
            };

            for (Position from : cornerPositions) {
                Piece piece = getPieceAt(from);
                if (piece != null && piece.getColor() == color) {
                    piecesChecked++;
                    System.out.println("  Угловая фигура " + piece.getClass().getSimpleName() +
                            " на " + from);

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

        if (!to.isValid(game.getGameType())) {
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
        removePieceAtInternal(from);
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
        GameType gameType = game.getGameType();

        // ВАЖНО: проверяем ВАЛИДНОСТЬ позиции для текущего типа игры
        if (!position.isValid(gameType)) {
            return null;
        }

        if (position.isCornerCell()) {
            // Угловые клетки - только для OMEGA
            if (gameType != GameType.OMEGA || cornerGrid == null) {
                return null;
            }
            return getCornerPiece(position);
        } else {
            // Основная доска
            // ВАЖНО: проверяем границы массива
            if (position.getRow() < 0 || position.getRow() >= BOARD_SIZE ||
                    position.getCol() < 0 || position.getCol() >= BOARD_SIZE) {
                return null;
            }
            return mainGrid[position.getRow()][position.getCol()];
        }
    }

    private Piece getCornerPiece(Position cornerPos) {
        int row = cornerPos.getRow();
        int col = cornerPos.getCol();

        if (row == -1 && col == -1) return cornerGrid[0][0]; // w1 - Белый Волшебник
        if (row == -1 && col == 10) return cornerGrid[0][1]; // w2 - Белый Волшебник
        if (row == 10 && col == -1) return cornerGrid[1][0]; // w3 - Черный Волшебник
        if (row == 10 && col == 10) return cornerGrid[1][1]; // w4 - Черный Волшебник

        return null;
    }

    private void setCornerPiece(Position cornerPos, Piece piece) {
        if (cornerGrid == null) return;

        int row = cornerPos.getRow();
        int col = cornerPos.getCol();

        if (row == -1 && col == -1) cornerGrid[0][0] = piece;
        else if (row == -1 && col == 10) cornerGrid[0][1] = piece;
        else if (row == 10 && col == -1) cornerGrid[1][0] = piece;
        else if (row == 10 && col == 10) cornerGrid[1][1] = piece;
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

        if (!to.isValid(game.getGameType())) {
            return false;
        }

        Color movingColor = piece.getColor();

        if (piece instanceof King) {
            King king = (King) piece;

            // === ОТЛАДОЧНЫЙ ВЫВОД ===
            System.out.println("\n=== DEBUG CASTLING CHECK ===");
            System.out.println("King at: " + from);
            System.out.println("Target: " + to);
            System.out.println("Game type: " + game.getGameType());

            List<CastlingInfo> possibleCastlingsDebug = MoveCalculator.getPossibleCastlings(king, this);
            System.out.println("Possible castlings found: " + possibleCastlingsDebug.size());

            for (CastlingInfo castling : possibleCastlingsDebug) {
                System.out.println("  - " + castling);
                System.out.println("    King to: " + castling.kingTo);
                System.out.println("    Compare with target: " + to + " -> equals: " + to.equals(castling.kingTo));
            }
            // === КОНЕЦ ОТЛАДОЧНОГО ВЫВОДА ===

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
                    removePieceAtInternal(castling.kingFrom);
                    setPieceAtInternal(king, castling.kingTo);
                    king.markAsMoved();

                    // Переместить ладью
                    removePieceAtInternal(castling.rookFrom);
                    setPieceAtInternal(rook, castling.rookTo);
                    ((Rook) rook).markAsMoved();

                    // Обновить состояние
                    game.clearEnPassantTarget();
                    calculateAttackMaps();

                    System.out.println("=== CASTLING SUCCESSFUL ===");
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
                    removePieceAtInternal(capturedPos);
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
        removePieceAtInternal(from);

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

        if (position.isCornerCell()) {
            setCornerPiece(position, piece);
        } else {
            mainGrid[position.getRow()][position.getCol()] = piece;
        }
    }

    private void removePieceAtInternal(Position position) {
        if (position.isCornerCell()) {
            setCornerPiece(position, null);
        } else {
            mainGrid[position.getRow()][position.getCol()] = null;
        }
    }

    public static boolean isLightSquare(Position pos) {
        return (pos.getRow() + pos.getCol()) % 2 == 1;
    }

    public static boolean isDarkSquare(Position pos) {
        return !isLightSquare(pos);
    }

    public void replacePiece(Position pos, Piece newPiece) {
        if (pos.isValid(game.getGameType())) {
            setPieceAtInternal(newPiece, pos);
        } else {
            throw new IllegalArgumentException("Некорректная позиция");
        }
    }

    private MoveSnapshot createSnapshotBeforeMove(Position from, Position to, Piece piece) {
        // Получаем фигуру на целевой клетке (если есть)
        Piece capturedPiece = getPieceAt(to);
        Position capturedPos = (capturedPiece != null) ? to : null;

        // Получаем информацию о рокировках - ИСПРАВЛЕНО для Omega Chess
        Position whiteKingPos = findKingPosition(Color.WHITE);
        Position blackKingPos = findKingPosition(Color.BLACK);
        King whiteKing = (whiteKingPos != null) ? (King) getPieceAt(whiteKingPos) : null;
        King blackKing = (blackKingPos != null) ? (King) getPieceAt(blackKingPos) : null;

        // Находим ладьи для рокировок - ИСПРАВЛЕНО: динамические координаты для разных типов игр
        Rook whiteRookKingSide = getRookAt(getRookKingSidePosition(Color.WHITE), Color.WHITE);
        Rook whiteRookQueenSide = getRookAt(getRookQueenSidePosition(Color.WHITE), Color.WHITE);
        Rook blackRookKingSide = getRookAt(getRookKingSidePosition(Color.BLACK), Color.BLACK);
        Rook blackRookQueenSide = getRookAt(getRookQueenSidePosition(Color.BLACK), Color.BLACK);

        // Проверяем, является ли ход рокировкой
        boolean isCastling = (piece instanceof King && Math.abs(from.getCol() - to.getCol()) == 2);

        // Проверяем, является ли ход взятием на проходе
        boolean isEnPassant = (piece instanceof Pawn && to.equals(game.getEnPassantTarget()));

        // Проверяем, является ли ход превращением пешки
        int lastRow = this.BOARD_SIZE - 1;
        boolean isPromotion = (piece instanceof Pawn &&
                ((piece.getColor() == Color.WHITE && to.getRow() == lastRow) ||
                        (piece.getColor() == Color.BLACK && to.getRow() == 0)));

        Piece castlingRook = null;
        if (isCastling) {
            // Определяем, какая ладья участвует в рокировке
            int rookCol = (to.getCol() > from.getCol()) ?
                    (game.getGameType() == GameType.CLASSIC ? 7 : 8) :  // Короткая
                    (game.getGameType() == GameType.CLASSIC ? 0 : 1);    // Длинная
            castlingRook = getPieceAt(new Position(from.getRow(), rookCol));
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

    // Вспомогательные методы для получения позиций ладей
    private Position getRookKingSidePosition(Color color) {
        int row = (color == Color.WHITE) ? 0 : (BOARD_SIZE - 1);
        int col = (game.getGameType() == GameType.CLASSIC) ? (BOARD_SIZE - 1) : (BOARD_SIZE - 2);
        return new Position(row, col);
    }

    private Position getRookQueenSidePosition(Color color) {
        int row = (color == Color.WHITE) ? 0 : (BOARD_SIZE - 1);
        int col = (game.getGameType() == GameType.CLASSIC) ? 0 : 1;
        return new Position(row, col);
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
            removePieceAtInternal(snapshot.to);
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
            GameType gameType = game.getGameType();
            Position rookFrom;
            Position rookTo;

            if (gameType == GameType.CLASSIC) {
                if (snapshot.to.getCol() == 6) { // Короткая рокировка (O-O)
                    rookFrom = new Position(snapshot.from.getRow(), 7);
                    rookTo = new Position(snapshot.from.getRow(), 5);
                } else { // Длинная рокировка (O-O-O)
                    rookFrom = new Position(snapshot.from.getRow(), 0);
                    rookTo = new Position(snapshot.from.getRow(), 3);
                }
            } else { // OMEGA
                if (snapshot.to.getCol() == 7) { // Короткая рокировка (O-O) f1→h1
                    rookFrom = new Position(snapshot.from.getRow(), 8); // i1
                    rookTo = new Position(snapshot.from.getRow(), 6);   // g1
                } else { // Длинная рокировка (O-O-O) f1→d1
                    rookFrom = new Position(snapshot.from.getRow(), 1); // b1
                    rookTo = new Position(snapshot.from.getRow(), 4);   // e1
                }
            }

            // Возвращаем ладью на место
            setPieceAtInternal(snapshot.castlingRook, rookFrom);
            removePieceAtInternal(rookTo);
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
        Position whiteKingPos = findKingPosition(Color.WHITE);
        Position blackKingPos = findKingPosition(Color.BLACK);
        King whiteKing = (whiteKingPos != null) ? (King) getPieceAt(whiteKingPos) : null;
        King blackKing = (blackKingPos != null) ? (King) getPieceAt(blackKingPos) : null;

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

        // Восстанавливаем состояние ладей - ИСПРАВЛЕНО: динамические координаты
        restoreRookState(getRookKingSidePosition(Color.WHITE), snapshot.whiteRookKingSideHasMovedBefore, Color.WHITE);
        restoreRookState(getRookQueenSidePosition(Color.WHITE), snapshot.whiteRookQueenSideHasMovedBefore, Color.WHITE);
        restoreRookState(getRookKingSidePosition(Color.BLACK), snapshot.blackRookKingSideHasMovedBefore, Color.BLACK);
        restoreRookState(getRookQueenSidePosition(Color.BLACK), snapshot.blackRookQueenSideHasMovedBefore, Color.BLACK);
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