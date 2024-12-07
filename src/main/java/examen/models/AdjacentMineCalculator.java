package examen.models;

import examen.models.BoardInterfaces.IAdjacentMineCalculator;

import java.util.Arrays;
import java.util.stream.IntStream;

/**
 * Calculador de minas adyacentes en el tablero.
 */
public class AdjacentMineCalculator implements IAdjacentMineCalculator {

    /**
     * Calcula el número de minas adyacentes para cada casilla vacía en el tablero.
     *
     * @param boxes Matriz de casillas del tablero.
     */
    @Override
    public void calculateAdjacentMines(Box[][] boxes) {
        int rows = boxes.length;
        int columns = boxes[0].length;

        IntStream.range(0, rows)
                .forEach(row -> IntStream.range(0, columns)
                        .filter(col -> !(boxes[row][col] instanceof MinedBox)) // Solo calcula para casillas no minadas.
                        .forEach(col -> {
                            int adjacentMineCount = countAdjacentMinesRecursive(boxes, row, col,
                                    new boolean[rows][columns]); // Inicializa el mapa de visitados.
                            ((EmptyBox) boxes[row][col]).setAdjacentMines(adjacentMineCount); // Asigna el conteo a la casilla vacía.
                        }));
    }

    /**
     * Cuenta las minas adyacentes de manera recursiva en torno a una posición específica.
     *
     * @param boxes Matriz de casillas del tablero.
     * @param row Fila actual.
     * @param col Columna actual.
     * @param visited Matriz de posiciones visitadas.
     * @return Número de minas adyacentes.
     */
    public int countAdjacentMinesRecursive(Box[][] boxes, int row, int col, boolean[][] visited) {
        int rows = boxes.length;
        int columns = boxes[0].length;

        // Verifica si la posición es válida o si ya fue visitada.
        if (!isValidPosition(row, col, rows, columns) || visited[row][col]) {
            return 0;
        }

        visited[row][col] = true; // Marca la posición como visitada.

        // Direcciones posibles para explorar adyacencias.
        int[][] directions = {
                { -1, -1 }, { -1, 0 }, { -1, 1 },
                { 0, -1 }, { 0, 1 },
                { 1, -1 }, { 1, 0 }, { 1, 1 }
        };

        return (int) Arrays.stream(directions)
                .filter(dir -> {
                    int newRow = row + dir[0];
                    int newCol = col + dir[1];
                    return isValidPosition(newRow, newCol, rows, columns) &&
                            boxes[newRow][newCol] instanceof MinedBox; // Verifica si la casilla adyacente contiene mina.
                })
                .count();
    }

    /**
     * Verifica si una posición es válida dentro de la matriz del tablero.
     *
     * @param row Fila a verificar.
     * @param col Columna a verificar.
     * @param rows Total de filas.
     * @param columns Total de columnas.
     * @return True si la posición es válida, false de lo contrario.
     */
    public boolean isValidPosition(int row, int col, int rows, int columns) {
        return row >= 0 && row < rows && col >= 0 && col < columns;
    }
}
