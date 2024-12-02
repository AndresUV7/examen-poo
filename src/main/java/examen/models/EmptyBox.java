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
// Clase hija Casilla Vacía
/**
 * Representa una casilla vacía en el tablero de Buscaminas.
 * Sigue el Principio de Responsabilidad Única.
 */
public class EmptyBox extends Box {
    @Override
    public boolean reveal() {
        if (!isRevealed() && !isFlagged()) {
            setRevealed(true);
            return true;
        }
        return false;
    }

    /**
     * Obtiene la cantidad de minas adyacentes.
     * @return número de minas adyacentes
     */
    public int getAdjacentMinesCount() {
        return getAdjacentMines();
    }
}
