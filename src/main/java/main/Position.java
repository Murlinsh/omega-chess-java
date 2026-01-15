package main;

public class Position {
    private final int row;
    private final int col;
    private final boolean isCornerCell;

    public Position(int row, int col) {
        this.row = row;
        this.col = col;
        this.isCornerCell = false;
    }

    // Конструктор для угловых клеток
    public Position(int row, int col, boolean isCornerCell) {
        this.row = row;
        this.col = col;
        this.isCornerCell = isCornerCell;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Position position = (Position) object;
        return row == position.row && col == position.col && isCornerCell == position.isCornerCell;
    }

    @Override
    public int hashCode() {
        return 31 * (31 * row + col) + (isCornerCell ? 1 : 0);
    }

    public boolean isValid(int boardSize) {
        return row >= 0 && row < boardSize && col >= 0 && col < boardSize;
    }

    // Дополнительный метод для работы с GameType
    public boolean isValid(GameType gameType) {
        int boardSize = gameType.getBoardSize();

        if (isCornerCell()) {
            // Угловые клетки валидны только для OMEGA
            if (gameType != GameType.OMEGA) return false;

            // Проверяем специальные координаты угловых клеток
            return (row == -1 && col == -1) ||     // Белый Чемпион
                    (row == -1 && col == 10) ||     // Белый Волшебник
                    (row == 10 && col == -1) ||     // Черный Чемпион
                    (row == 10 && col == 10);       // Черный Волшебник
        } else {
            // Основная доска
            return row >= 0 && row < boardSize && col >= 0 && col < boardSize;
        }
    }

    public boolean isCornerCell() {
        return isCornerCell;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    @Override
    public String toString() {
        if (isCornerCell) {
            return "Corner(" + row + "," + col + ")";
        }
        // Конвертация для шахматной нотации
        char file = (char) ('a' + col);
        int rank = row + 1;
        return "" + file + rank;
    }

    // Метод для проверки, находится ли позиция на основной доске (не угловая)
    public boolean isOnMainBoard() {
        return !isCornerCell;
    }

    // Метод для получения смежной позиции на основной доске (для угловых клеток)
    public Position getAdjacentMainBoardPosition(Color color) {
        if (!isCornerCell) {
            return this;
        }

        // Угловые клетки примыкают к краю основной доски
        if (row == -1 && col == -1) { // Белый Чемпион
            return new Position(0, 0); // Примыкает к a1
        } else if (row == -1 && col == 10) { // Белый Волшебник
            return new Position(0, 9); // Примыкает к j1
        } else if (row == 10 && col == -1) { // Черный Чемпион
            return new Position(9, 0); // Примыкает к a10
        } else if (row == 10 && col == 10) { // Черный Волшебник
            return new Position(9, 9); // Примыкает к j10
        }
        return null;
    }

    public String getOmegaCornerName() {
        if (!isCornerCell()) return null;

        if (row == -1 && col == -1) return "w1";
        if (row == -1 && col == 10) return "w2";
        if (row == 10 && col == -1) return "w3";
        if (row == 10 && col == 10) return "w4";
        return null;
    }

    public static Position createCornerPosition(String cornerName) {
        switch(cornerName) {
            case "w1": return new Position(-1, -1, true);
            case "w2": return new Position(-1, 10, true);
            case "w3": return new Position(10, -1, true);
            case "w4": return new Position(10, 10, true);
            default: return null;
        }
    }
}