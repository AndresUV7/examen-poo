package examen.models;

import examen.models.GameInterfaces.IBoxDisplayStrategy;

/**
 * Estrategia predeterminada para mostrar la representación de una casilla.
 */
public class DefaultBoxDisplayStrategy implements IBoxDisplayStrategy {

    /**
     * Obtiene la representación en texto de una casilla.
     *
     * @param box La casilla a representar.
     * @return Representación de la casilla: "X" para mina, número de minas adyacentes, "F" si está marcada, "?" si está oculta.
     */
    @Override
    public String getRepresentation(Box box) {
        if (box.isRevealed()) {
            return box instanceof MinedBox ? "X" : 
                String.valueOf(((EmptyBox) box).getAdjacentMinesCount()); // Representa el número de minas adyacentes.
        } else if (box.isFlagged()) {
            return "F"; // Casilla marcada con bandera.
        }
        return "?"; // Casilla oculta.
    }
}
