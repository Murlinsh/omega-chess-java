package main.pieces;

import main.*;

import java.util.ArrayList;
import java.util.List;

public class Wizard extends Piece {
    public Wizard(Color color, Position startPosition) {
        super(color, startPosition);
    }

    public List<Position> getPossibleMoves(Board board) {
        return MoveCalculator.getWizardMoves(this, board);
    }

    @Override
    public List<Position> getAttackingSquares(Board board) {
        return getPossibleMoves(board);
    }
}
