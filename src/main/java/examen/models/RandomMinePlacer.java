package examen.models;

import examen.models.BoardInterfaces.IMineStrategy;

import java.util.Random;

/**
 * Estrategia para colocar minas de manera aleatoria en el tablero.
 */
public class RandomMinePlacer implements IMineStrategy {

    /**
     * Coloca las minas en posiciones aleatorias dentro del tablero.
     *
     * @param boxes Matriz de casillas del tablero.
     * @param totalMines Número total de minas a colocar.
     * @param random Generador de números aleatorios.
     */
    @Override
    public void placeMines(Box[][] boxes, int totalMines, Random random) {
        int placedMines = 0; // Contador de minas colocadas.
        int rows = boxes.length;
        int columns = boxes[0].length;

        // Continúa hasta colocar todas las minas.
        while (placedMines < totalMines) {
            int randomRow = random.nextInt(rows); // Fila aleatoria.
            int randomColumn = random.nextInt(columns); // Columna aleatoria.

            // Solo coloca una mina si la casilla aún no contiene una.
            if (!(boxes[randomRow][randomColumn] instanceof MinedBox)) {
                MinedBox minedBox = new MinedBox();
                minedBox.setXPosition(randomRow);
                minedBox.setYPosition(randomColumn);
                minedBox.setMine(true); // Marca la casilla como mina.
                boxes[randomRow][randomColumn] = minedBox;
                placedMines++; // Incrementa el contador de minas colocadas.
            }
        }
    }
}
