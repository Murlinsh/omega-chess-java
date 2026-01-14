package main.pieces;

import main.*;

import java.util.ArrayList;
import java.util.List;

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
        GameType gameType = board.getGame().getGameType();

        // Вперёд-вправо
        Position forwardRight = new Position(
                this.getPosition().getRow() + offset_forward,
                this.getPosition().getCol() + 1
        );
        if (forwardRight.isValid(gameType)) {
            moves.add(forwardRight);
        }
        // Вперёд-влево
        Position forwardLeft = new Position(
                this.getPosition().getRow() + offset_forward,
                this.getPosition().getCol() - 1
        );
        if (forwardLeft.isValid(gameType)) {
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