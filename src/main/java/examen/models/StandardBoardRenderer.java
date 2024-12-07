package examen.models;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import examen.models.GameInterfaces.IBoardRenderer;
import examen.models.GameInterfaces.IBoxDisplayStrategy;
import lombok.RequiredArgsConstructor;

/**
 * Renderizador estándar para mostrar el tablero de Minesweeper.
 */
@RequiredArgsConstructor
public class StandardBoardRenderer implements IBoardRenderer {

    private final IBoxDisplayStrategy displayStrategy; // Estrategia para obtener representaciones de casillas.

    /**
     * Imprime el tablero en su estado actual utilizando la estrategia predeterminada.
     *
     * @param board Tablero a imprimir.
     */
    @Override
    public void printBoard(Board board) {
        printBoardWithStrategy(board, displayStrategy); // Usa la estrategia definida para imprimir.
    }

    /**
     * Imprime una vista detallada del tablero, mostrando información adicional.
     *
     * @param board Tablero a imprimir.
     */
    @Override
    public void printDetailedBoard(Board board) {
        printBoardWithStrategy(board, this::getDetailedBoxRepresentation); // Usa una estrategia detallada.
    }

    /**
     * Método auxiliar para imprimir el tablero utilizando una estrategia dada.
     *
     * @param board Tablero a imprimir.
     * @param strategy Estrategia para obtener las representaciones de las casillas.
     */
    private void printBoardWithStrategy(Board board, IBoxDisplayStrategy strategy) {
        // Imprime los encabezados de columnas.
        String columnLabels = IntStream.range(1, board.getColumns() + 1)
                .mapToObj(String::valueOf)
                .collect(Collectors.joining(" ", "  ", "\n"));
        System.out.print(columnLabels);

        // Imprime las filas del tablero.
        IntStream.range(0, board.getRows())
                .mapToObj(row -> {
                    String rowRepresentation = Arrays.stream(board.getBoxes()[row])
                            .map(strategy::getRepresentation)
                            .collect(Collectors.joining(" "));
                    return String.format("%c %s\n", 'A' + row, rowRepresentation); // Etiqueta las filas con letras.
                })
                .forEach(System.out::print);
    }

    /**
     * Obtiene una representación detallada de una casilla.
     *
     * @param box La casilla a representar.
     * @return Representación detallada: "X" para mina, número de minas adyacentes, "F" si está marcada, "?" si está oculta.
     */
    private String getDetailedBoxRepresentation(Box box) {
        if (box.isRevealed()) {
            return box instanceof MinedBox ? "X" : 
                String.valueOf(((EmptyBox) box).getAdjacentMinesCount()); // Representa el número de minas adyacentes.
        } else if (box.isFlagged()) {
            return "F"; // Casilla marcada con bandera.
        }
        return "?"; // Casilla oculta.
    }
}
