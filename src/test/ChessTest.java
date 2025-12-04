public class ChessTest {
    public static void main(String[] args) {
        System.out.println("\n=== ТЕСТИРОВАНИЕ Game.makeMove() ===");

        Game game = new Game(GameType.CLASSIC);

        // Тест 1: Белые ходят первыми
        System.out.println("\n1. Белые ходят пешкой e2-e4:");
        boolean success = game.makeMove(new Position(1, 4), new Position(3, 4));
        System.out.println("   Результат: " + (success ? "✓ Успех" : "✗ Ошибка"));

        // Тест 2: Белые пытаются ходить дважды подряд (ошибка)
        System.out.println("\n2. Белые пытаются ходить снова (ошибка):");
        success = game.makeMove(new Position(1, 3), new Position(2, 3));
        System.out.println("   Результат: " + (success ? "✗ ДОЛЖНА БЫТЬ ОШИБКА" : "✓ Правильно запрещено"));

        // Тест 3: Чёрные делают ответный ход
        System.out.println("\n3. Чёрные отвечают e7-e5:");
        success = game.makeMove(new Position(6, 4), new Position(4, 4));
        System.out.println("   Результат: " + (success ? "✓ Успех" : "✗ Ошибка"));

        // Тест 4: Белые ходят конём
        System.out.println("\n4. Белые ходят конём g1-f3:");
        success = game.makeMove(new Position(0, 6), new Position(2, 5));
        System.out.println("   Результат: " + (success ? "✓ Успех" : "✗ Ошибка"));
    }
}