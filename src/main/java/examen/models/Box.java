package examen.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

// Implementa patrón builder
@SuperBuilder
// Genera getters, setters, toString, etc
@Data
// Genera constructor vacío
@NoArgsConstructor
// Clase base Casilla
public abstract class Box {
    private int xPosition;
    private int yPosition;
    private boolean isMine;
    private boolean isRevealed;
    private boolean isFlagged;
    private int adjacentMines;

    /**
     * Método abstracto para revelar la casilla.
     * 
     * @return true si la casilla puede ser revelada, false en caso contrario
     */
    public abstract boolean reveal();

    /**
     * Verifica si la casilla es segura para revelar.
     * 
     * @return true si la casilla no contiene una mina
     */
    public boolean isSafe() {
        return !isMine;
    }
}
