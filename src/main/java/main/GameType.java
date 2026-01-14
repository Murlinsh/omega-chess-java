package main;

public enum GameType {
    CLASSIC(8, 2),  // доска 8×8, пешка: первый ход до 2 клеток
    OMEGA(10, 3);   // доска 10×10, пешка: первый ход до 3 клеток

    private final int boardSize;
    private final int pawnInitialMaxSteps;

    GameType(int boardSize, int steps) {
        this.boardSize = boardSize;
        this.pawnInitialMaxSteps = steps;
    }

    public int getBoardSize() {
        return boardSize;
    }

    public int getPawnInitialMaxSteps() {
        return pawnInitialMaxSteps;
    }
}