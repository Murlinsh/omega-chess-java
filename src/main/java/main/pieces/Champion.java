package main.pieces;

import main.*;

import java.util.ArrayList;
import java.util.List;

public class Champion extends Piece {
    public Champion(Color color, Position startPosition) {
        super(color, startPosition);
    }

    public List<Position> getPossibleMoves(Board board) {
        return MoveCalculator.getChampionMoves(this, board);
    }

    @Override
    public List<Position> getAttackingSquares(Board board) {
        return getPossibleMoves(board);
    }
}