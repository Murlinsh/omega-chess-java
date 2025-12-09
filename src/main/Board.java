package main;

public class Board {
    private Piece[][] grid;
    private int BOARD_SIZE;
    // Также нужно отметить: a1 -> [0][0], h8 -> [7][7]
    private final Game game;

    public Board(Game game) {
        this.game = game;
        if (this.game.getGameType() == GameType.CLASSIC) {
            this.BOARD_SIZE = 8;
            this.grid = new Piece[8][8];
        } else {

        }
    }

    // Расстановка фигур на доску
    public void initializePieces() {
        GameType type = game.getGameType(); // Получаем тип игры из Game
        if (type == GameType.CLASSIC) {
            setupClassicBoard();
        } else {
            setupOmegaBoard();
        }
    }

    private void setupClassicBoard() {
        // 1. Белые пешки (ряд 2 = row=1)
        for (int col = 0; col < 8; col++) {
            placePiece(new Pawn(Color.WHITE, new Position(1, col)));
        }

        // 2. Чёрные пешки (ряд 7 = row=6)
        for (int col = 0; col < 8; col++) {
            placePiece(new Pawn(Color.BLACK, new Position(6, col)));
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
        placePiece(new Rook(Color.BLACK, new Position(7, 7)));
        placePiece(new Knight(Color.BLACK, new Position(7, 6)));
        placePiece(new Bishop(Color.BLACK, new Position(7, 5)));
        placePiece(new King(Color.BLACK, new Position(7, 4)));
        placePiece(new Queen(Color.BLACK, new Position(7, 3)));
        placePiece(new Bishop(Color.BLACK, new Position(7, 2)));
        placePiece(new Knight(Color.BLACK, new Position(7, 1)));
        placePiece(new Rook(Color.BLACK, new Position(7, 0)));
    }

    private void setupOmegaBoard() {
        // 1. Создаём доску 10×10 с угловыми клетками
        // 2. Расставляем фигуры на новых позициях
        // 3. Добавляем Champion и Wizard
        // 4. Белые пешки на row=8, чёрные на row=3? (нужно уточнить правила)
    }

    public Game getGame() {
        return game;
    }

    public Piece getPieceAt(Position position) {
        if (!position.isValid(8)) {
            return null;
        }
        return grid[position.getRow()][position.getCol()];
    }

    // Для начальной расстановки
    public void placePiece(Piece piece) {
        setPieceAtInternal(piece, piece.getPosition());
    }

    // Для ходов игрока
    public boolean movePiece(Position from, Position to) {
        Piece piece = getPieceAt(from);
        if (piece == null) {
            return false; // Нечего перемещать
        }

        if (!to.isValid(8)) {
            return false; // За пределами доски
        }

        // Проверяем, разрешён ли такой ход для фигуры
        if (!piece.getPossibleMoves(this).contains(to)) {
            return false; // Фигура не может так ходить
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

        return true; // Ход выполнен
    }

    private void setPieceAtInternal(Piece piece, Position position) {
        if (piece != null) {
            piece.setPosition(position);
        }
        grid[position.getRow()][position.getCol()] = piece;
    }


    public static boolean isLightSquare(Position pos) {
        return (pos.getRow() + pos.getCol()) % 2 == 0;
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
}
