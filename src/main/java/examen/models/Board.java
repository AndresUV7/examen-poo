package examen.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.util.Random;
import java.util.stream.IntStream;

import examen.models.BoardInterfaces.IAdjacentMineCalculator;
import examen.models.BoardInterfaces.IBoardGenerator;
import examen.models.BoardInterfaces.IBoardValidator;
import examen.models.BoardInterfaces.IMineStrategy;

/**
 * Representa un tablero para el juego Buscaminas.
 */
@Builder
@Data
@AllArgsConstructor
public class Board implements IBoardGenerator {
    private int rows; // Número de filas del tablero.
    private int columns; // Número de columnas del tablero.
    private int totalMines; // Número total de minas en el tablero.
    private Box[][] boxes; // Matriz que contiene las casillas del tablero.

    @Builder.Default
    private final Random random = new Random(); // Generador de números aleatorios.

    @Builder.Default
    private int flagCount = 0; // Contador de banderas colocadas en el tablero.

    @Builder.Default
    private IBoardValidator boardValidator = new StandardBoardValidator(); // Validador del tablero.

    @Builder.Default
    private IMineStrategy mineStrategy = new RandomMinePlacer(); // Estrategia para colocar minas.

    @Builder.Default
    private IAdjacentMineCalculator adjacentMineCalculator = new AdjacentMineCalculator(); // Calculador de minas adyacentes.

    /**
     * Genera un tablero con minas y valores iniciales.
     *
     * @param boxes Matriz de casillas del tablero.
     * @param totalMines Número total de minas.
     */
    @Override
    public void generate(Box[][] boxes, int totalMines) {
        boardValidator.validate(rows, columns, totalMines); // Validación del tablero.
        initializeEmptyBoard(); // Inicialización del tablero vacío.
        mineStrategy.placeMines(this.boxes, totalMines, random); // Coloca las minas.
        adjacentMineCalculator.calculateAdjacentMines(this.boxes); // Calcula las minas adyacentes.
    }

    /**
     * Genera el tablero utilizando los parámetros actuales.
     */
    public void generateBoard() {
        generate(boxes, totalMines);
    }

    /**
     * Inicializa un tablero vacío con casillas sin minas.
     */
    public void initializeEmptyBoard() {
        boxes = IntStream.range(0, rows)
                .mapToObj(i -> IntStream.range(0, columns)
                        .mapToObj(j -> {
                            EmptyBox emptyBox = new EmptyBox();
                            emptyBox.setXPosition(i);
                            emptyBox.setYPosition(j);
                            return emptyBox;
                        })
                        .toArray(Box[]::new))
                .toArray(Box[][]::new);
    }

    /**
     * Verifica si una posición es válida en el tablero.
     *
     * @param row Fila a verificar.
     * @param col Columna a verificar.
     * @return True si la posición es válida, de lo contrario, false.
     */
    public boolean isValidPosition(int row, int col) {
        return row >= 0 && row < rows && col >= 0 && col < columns;
    }

    /**
     * Revela las casillas adyacentes de manera recursiva.
     *
     * @param row Fila de inicio.
     * @param col Columna de inicio.
     * @return Número de banderas removidas durante la revelación.
     */
    public int revealAdjacent(int row, int col) {
        if (!isValidPosition(row, col))
            return 0;

        Box box = boxes[row][col];

        if (box.isRevealed())
            return 0;

        int flagsRemoved = 0;
        if (box.isFlagged()) {
            box.setFlagged(false);
            flagsRemoved++;
        }

        box.reveal(); // Revela la casilla actual.

        if (box instanceof EmptyBox && ((EmptyBox) box).getAdjacentMinesCount() > 0) {
            return flagsRemoved;
        }

        // Direcciones para explorar las casillas adyacentes.
        int[][] directions = {
                { -1, -1 }, { -1, 0 }, { -1, 1 },
                { 0, -1 }, { 0, 1 },
                { 1, -1 }, { 1, 0 }, { 1, 1 }
        };

        for (int[] direction : directions) {
            int newRow = row + direction[0];
            int newCol = col + direction[1];
            flagsRemoved += revealAdjacent(newRow, newCol);
        }

        return flagsRemoved;
    }

    /**
     * Obtiene el número actual de banderas en el tablero.
     *
     * @return Número de banderas colocadas.
     */
    public int getFlagCount() {
        return (int) IntStream.range(0, rows)
                .flatMap(row -> IntStream.range(0, columns)
                        .filter(col -> boxes[row][col].isFlagged()))
                .count();
    }

    /**
     * Incrementa el contador de banderas si no se excede el total de minas.
     */
    public void increaseFlagCount() {
        if (flagCount < totalMines) {
            flagCount++;
        } else {
            throw new IllegalStateException("Cannot place more flags than total mines.");
        }
    }

    /**
     * Decrementa el contador de banderas si es mayor a cero.
     */
    public void decreaseFlagCount() {
        if (flagCount > 0) {
            flagCount--;
        } else {
            throw new IllegalStateException("Flag count cannot be negative.");
        }
    }

    /**
     * Verifica si todas las casillas no minadas han sido reveladas.
     *
     * @return True si todas las casillas no minadas están reveladas, de lo contrario, false.
     */
    public boolean allNonMinedBoxesRevealed() {
        return IntStream.range(0, rows)
                .flatMap(i -> IntStream.range(0, columns)
                        .filter(j -> !boxes[i][j].isMine() && !boxes[i][j].isRevealed()))
                .count() == 0;
    }
}
