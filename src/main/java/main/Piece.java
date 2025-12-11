package main;
import java.util.List;

public abstract class Piece {
    private final Color color;
    private Position position;

    public Piece(Color color, Position startPosition) {
        this.color = color;
        this.position = startPosition;
    }

    public Color getColor() {
        return this.color;
    }

    // Функция для проверки, является ли другая фигура вражеской
    public boolean isOpponent(Piece otherPiece) {
        return otherPiece != null && this.color != otherPiece.getColor();
    }

    public Position getPosition() {
        return this.position;
    }
    public void setPosition(Position newPosition) {
        this.position = newPosition;
    }

    public abstract List<Position> getPossibleMoves(Board board);

    public List<Position> getAttackingSquares(Board board) {
        // По умолчанию = возможные ходы
        return getPossibleMoves(board);
    }
}
