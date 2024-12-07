package examen.models;

/**
 * Contiene las interfaces necesarias para las operaciones y visualización del juego Buscaminas.
 */
public interface GameInterfaces {

    /**
     * Define el comportamiento para renderizar el tablero.
     */
    interface IBoardRenderer {
        /**
         * Imprime el tablero en su estado actual.
         *
         * @param board Tablero a imprimir.
         */
        void printBoard(Board board);

        /**
         * Imprime una vista detallada del tablero, incluyendo información adicional.
         *
         * @param board Tablero a imprimir.
         */
        void printDetailedBoard(Board board);
    }

    /**
     * Define el comportamiento para obtener la representación visual de una casilla.
     */
    interface IBoxDisplayStrategy {
        /**
         * Obtiene la representación visual de una casilla.
         *
         * @param box La casilla a representar.
         * @return Representación en forma de texto.
         */
        String getRepresentation(Box box);
    }

    /**
     * Define una acción que se puede realizar en el juego.
     */
    interface IGameAction {
        /**
         * Ejecuta una acción específica en el juego.
         *
         * @param game Instancia del juego en la que se ejecutará la acción.
         */
        void execute(Game game);
    }

    /**
     * Define el comportamiento para manipular el estado del tablero.
     */
    interface IBoardManipulation {
        /**
         * Revela todas las casillas del tablero.
         */
        void revealAllBoxes();

        /**
         * Revela las casillas adyacentes a una posición específica.
         *
         * @param row Fila de la casilla.
         * @param col Columna de la casilla.
         * @return Número de banderas removidas durante la revelación.
         */
        int revealAdjacent(int row, int col);
    }
}
