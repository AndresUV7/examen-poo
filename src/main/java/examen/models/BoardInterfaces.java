package examen.models;

import java.util.Random;

/**
 * Contiene las interfaces necesarias para implementar el comportamiento del tablero de Minesweeper.
 */
public interface BoardInterfaces {

    /**
     * Define el comportamiento para la generación del tablero.
     */
    interface IBoardGenerator {
        /**
         * Genera un tablero con una configuración inicial de casillas y minas.
         *
         * @param boxes Matriz de casillas del tablero.
         * @param totalMines Número total de minas a colocar.
         */
        void generate(Box[][] boxes, int totalMines);
    }

    /**
     * Define el comportamiento para la validación del tablero.
     */
    interface IBoardValidator {
        /**
         * Valida los parámetros de configuración del tablero.
         *
         * @param rows Número de filas.
         * @param columns Número de columnas.
         * @param totalMines Número total de minas.
         */
        void validate(int rows, int columns, int totalMines);
    }

    /**
     * Define el comportamiento para la colocación de minas en el tablero.
     */
    interface IMineStrategy {
        /**
         * Coloca minas en el tablero de acuerdo con una estrategia específica.
         *
         * @param boxes Matriz de casillas del tablero.
         * @param totalMines Número total de minas a colocar.
         * @param random Generador de números aleatorios.
         */
        void placeMines(Box[][] boxes, int totalMines, Random random);
    }

    /**
     * Define el comportamiento para calcular las minas adyacentes.
     */
    interface IAdjacentMineCalculator {
        /**
         * Calcula el número de minas adyacentes para cada casilla en el tablero.
         *
         * @param boxes Matriz de casillas del tablero.
         */
        void calculateAdjacentMines(Box[][] boxes);
    }
}
