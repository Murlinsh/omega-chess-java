package main;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== Omega Chess ===");
        System.out.println("Запуск GUI версии...");

        try {
            // Запускаем JavaFX приложение
            main.gui.ChessApp.main(args);
        } catch (Exception e) {
            System.err.println("Ошибка запуска JavaFX: " + e.getMessage());
            e.printStackTrace();

            // Запасной вариант: консольная версия
            System.out.println("Запуск консольной версии...");
            ConsoleChess chess = new ConsoleChess(GameType.CLASSIC);
            chess.startGame();
        }
    }
}