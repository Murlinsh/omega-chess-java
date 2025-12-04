public class Position {
    private final int row;
    private final int col;

    public Position(int row, int col) {
        this.row = row;
        this.col = col;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true; // ЭТО ОДИН И ТОТ ЖЕ ОБЪЕКТ
        if (object == null || getClass() != object.getClass()) return false;
        Position position = (Position) object;
        return row == position.row && col == position.col;
    }

    // МЕТОД hashCode() будет использован как инструмент для быстрой проверки внутри hashSet и/или hashMap
    @Override
    public int hashCode() {
        // Стандартный способ: объединяем хэши полей
        // 31 — небольшое простое число, снижает коллизии
        return 31 * row + col;
    }

    public boolean isValid(int boardSize) {
        return row >= 0 && row < boardSize && col >= 0 && col < boardSize;
    }

    public int getRow() {
        return row;
    }

    public int getCol(){
        return col;
    }
}
