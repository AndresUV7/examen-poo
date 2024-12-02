package examen.models;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

// Implementa el patrón builder
@SuperBuilder
// Genera getters, setters, toString, etc.
@Data
// Genera equals y hashCode incluyendo los atributos de la clase padre
@EqualsAndHashCode(callSuper = true)
// Genera constructor vacío
@NoArgsConstructor
// Clase hija Casilla Minada
/**
 * Representa una casilla que contiene una mina.
 * Sigue el Principio de Responsabilidad Única.
 */
public class MinedBox extends Box {
    private boolean exploded;

    @Override
    public boolean reveal() {
        if (!isRevealed() && !isFlagged()) {
            setRevealed(true);
            if (isMine()) {
                explode();
                return false;
            }
            return true;
        }
        return false;
    }

    /**
     * Activa la explosión de la mina.
     */
    public void explode() {
        setExploded(true);
    }
}
