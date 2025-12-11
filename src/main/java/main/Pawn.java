package main;

import java.util.ArrayList;
import java.util.List;

import static main.MoveCalculator.BOARD_SIZE;

public class Pawn extends Piece {
    private boolean hasMoved = false;

    public Pawn(Color color, Position startPosition) {
        super(color, startPosition);
    }

    public List<Position> getPossibleMoves(Board board) {
        return MoveCalculator.getPawnMoves(this, board);
    }

    @Override
    public List<Position> getAttackingSquares(Board board) {
        // Только диагональные атаки
        List<Position> moves = new ArrayList<>();
        int offset_forward = this.getColor() == Color.WHITE ? +1 : -1;

        // Вперёд-вправо
        Position forwardRight = new Position(
                this.getPosition().getRow() + offset_forward,
                this.getPosition().getCol() + 1
        );
        if (forwardRight.isValid(BOARD_SIZE)) {
            moves.add(forwardRight);
        }
        // Вперёд-влево
        Position forwardLeft = new Position(
                this.getPosition().getRow() + offset_forward,
                this.getPosition().getCol() - 1
        );
        if (forwardLeft.isValid(BOARD_SIZE)) {
            moves.add(forwardLeft);
        }
        return moves;
    }

    // Метод для обновления состояния после хода
    public void markAsMoved() {
        hasMoved = true;
    }

    public boolean hasMoved() {
        return hasMoved;
    }

    public void resetMoved() {
        hasMoved = false;
    }
}