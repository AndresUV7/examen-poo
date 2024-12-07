package examen.models;

import java.util.Arrays;

import examen.models.GameInterfaces.IBoardManipulation;
import examen.models.GameInterfaces.IBoardRenderer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Representa el estado y las acciones principales del juego Buscaminas.
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Game implements IBoardManipulation {
    private Board board; // Tablero del juego.
    private Player player; // Jugador asociado al juego.

    @Builder.Default
    private IBoardRenderer renderer = new StandardBoardRenderer(new DefaultBoxDisplayStrategy()); // Renderizador del tablero.

    /**
     * Revela todas las casillas del tablero.
     */
    @Override
    public void revealAllBoxes() {
        Arrays.stream(board.getBoxes())
                .flatMap(Arrays::stream)
                .forEach(Box::reveal); // Revela cada casilla del tablero.
    }

    /**
     * Revela las casillas adyacentes a una posición dada.
     *
     * @param row Fila de la casilla.
     * @param col Columna de la casilla.
     * @return Número de banderas removidas durante la revelación.
     */
    @Override
    public int revealAdjacent(int row, int col) {
        return board.revealAdjacent(row, col);
    }

    /**
     * Obtiene el número actual de banderas en el tablero.
     *
     * @return Número de banderas colocadas.
     */
    public int getFlagCount() {
        return board.getFlagCount();
    }

    /**
     * Incrementa el contador de banderas en el tablero.
     */
    public void increaseFlagCount() {
        board.setFlagCount(board.getFlagCount() + 1); // Actualiza el contador de banderas.
    }

    /**
     * Decrementa el contador de banderas en el tablero.
     */
    public void decreaseFlagCount() {
        board.setFlagCount(board.getFlagCount() - 1); // Actualiza el contador de banderas.
    }

    /**
     * Imprime el tablero en su estado actual.
     */
    public void printBoard() {
        renderer.printBoard(board);
    }

    /**
     * Imprime una vista detallada del tablero, incluyendo información adicional.
     */
    public void printDetailedBoard() {
        renderer.printDetailedBoard(board);
    }

    /**
     * Configura el estado de fin del juego.
     *
     * @param b True si el juego está terminado, de lo contrario, false.
     */
    public void setGameOver(boolean b) {
        // TODO Implementar el comportamiento para finalizar el juego.
        throw new UnsupportedOperationException("Unimplemented method 'setGameOver'");
    }
}
