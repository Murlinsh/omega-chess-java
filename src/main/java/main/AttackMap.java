package main;

import main.pieces.Piece;

import java.util.*;

public class AttackMap {
    // Map<Position, List<Piece>> - какие фигуры атакуют данную позицию
    private Map<Position, List<Piece>> attackMap;

    public AttackMap() {
        attackMap = new HashMap<>();
    }

    // Добавить атакующую фигуру на позицию
    public void addAttack(Position position, Piece attacker) {
        attackMap.computeIfAbsent(position, k -> new ArrayList<>()).add(attacker);
    }

    // Получить список фигур, атакующих позицию
    public List<Piece> getAttackers(Position position) {
        return attackMap.getOrDefault(position, Collections.emptyList());
    }

    // Проверить, атакована ли позиция
    public boolean isAttacked(Position position) {
        return attackMap.containsKey(position) && !attackMap.get(position).isEmpty();
    }

    // Очистить карту (перед пересчётом)
    public void clear() {
        attackMap.clear();
    }
}
