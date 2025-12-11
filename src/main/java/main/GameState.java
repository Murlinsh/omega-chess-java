package main;

public final class GameState {
    // Этот класс пока не реализован
    // Используется для будущей реализации сохранения состояний игры

    private GameState() {
        // Приватный конструктор - нельзя создавать экземпляры
    }

    // Статический метод для проверки, реализован ли функционал
    public static boolean isImplemented() {
        return false;
    }

    public static String getStatus() {
        return "GameState не реализован. Используется MoveSnapshot для отмены ходов.";
    }
}