package main;
public enum GameType {
    CLASSIC(2),  // Пешка: первый ход до 2 клеток
    OMEGA(3);    // Пешка: первый ход до 3 клеток

    private final int pawnInitialMaxSteps;

    GameType(int steps) {
        this.pawnInitialMaxSteps = steps;
    }

    public int getPawnInitialMaxSteps() {
        return pawnInitialMaxSteps;
    }
}