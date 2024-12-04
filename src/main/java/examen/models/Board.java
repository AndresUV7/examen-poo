package examen.models;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.IntStream;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class Board {
    private int rows;
    private int columns;
    private int totalMines;
    private Box[][] boxes;

    @Builder.Default
    private final Random random = new Random();

    @Builder.Default
    private int flagCount = 0;

    public Board(Integer rows, Integer columns, Integer totalMines, Box[][] boxes, Random random) {
        this.rows = rows;
        this.columns = columns;
        this.totalMines = totalMines;
        this.boxes = boxes;
        this.random = random != null ? random : new Random();
    }

    /**
     * Genera el tablero del juego con minas colocadas aleatoriamente.
     */
    public void generateBoard() {
        validateBoardParameters();
        initializeEmptyBoard();
        placeMines();
        calculateAdjacentMines();
    }

    /**
     * Valida los parámetros de generación del tablero.
     * 
     * @throws IllegalArgumentException si los parámetros son inválidos
     */
    private void validateBoardParameters() {
        if (rows <= 0 || columns <= 0) {
            throw new IllegalArgumentException("Las dimensiones del tablero deben ser positivas");
        }

        int maxPossibleMines = rows * columns;
        if (totalMines < 0 || totalMines > maxPossibleMines) {
            throw new IllegalArgumentException("Número de minas inválido: " + totalMines);
        }
    }

    /**
     * Inicializa el tablero con casilleros vacías.
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
     * Coloca minas aleatoriamente en el tablero.
     */
    private void placeMines() {
        int placedMines = 0;
        while (placedMines < totalMines) {
            int randomRow = random.nextInt(rows);
            int randomColumn = random.nextInt(columns);

            // Solo colocar una mina si la caja actual no es ya una mina
            if (!(boxes[randomRow][randomColumn] instanceof MinedBox)) {
                MinedBox minedBox = new MinedBox();
                minedBox.setXPosition(randomRow);
                minedBox.setYPosition(randomColumn);
                minedBox.setMine(true);
                boxes[randomRow][randomColumn] = minedBox;
                placedMines++;
            }
        }
    }

    /**
     * Calcula el número de minas adyacentes para cada casilla vacía del tablero.
     */
    public void calculateAdjacentMines() {
        IntStream.range(0, rows)
                .forEach(row -> IntStream.range(0, columns)
                        .filter(col -> !(boxes[row][col] instanceof MinedBox))
                        .forEach(col -> {
                            int adjacentMineCount = countAdjacentMinesRecursive(row, col,
                                    new boolean[rows][columns]);
                            ((EmptyBox) boxes[row][col]).setAdjacentMines(adjacentMineCount);
                        }));
    }

    /**
     * Cuenta recursivamente el número de minas en las posiciones adyacentes a una
     * casilla.
     * 
     * @param row     Fila de la casilla actual
     * @param col     Columna de la casilla actual
     * @param visited Matriz de seguimiento de casillas visitadas para evitar
     *                recursión infinita
     * @return Número de minas adyacentes
     */
    private int countAdjacentMinesRecursive(int row, int col, boolean[][] visited) {
        // Base case: check if position is invalid or already visited
        if (!isValidPosition(row, col) || visited[row][col]) {
            return 0;
        }

        // Mark current position as visited to prevent infinite recursion
        visited[row][col] = true;

        // Define potential adjacent positions (8 directions)
        int[][] directions = {
                { -1, -1 }, { -1, 0 }, { -1, 1 },
                { 0, -1 }, { 0, 1 },
                { 1, -1 }, { 1, 0 }, { 1, 1 }
        };

        // Cuenta las minas adyacentes usando stream
        return (int) Arrays.stream(directions)
                .filter(dir -> {
                    int newRow = row + dir[0];
                    int newCol = col + dir[1];
                    return isValidPosition(newRow, newCol) &&
                            boxes[newRow][newCol] instanceof MinedBox;
                })
                .count();
    }

    /**
     * Verifica si la posición está dentro de los límites del tablero.
     * 
     * @param row Fila a verificar
     * @param col Columna a verificar
     * @return true si la posición es válida, false en caso contrario
     */
    public boolean isValidPosition(int row, int col) {
        return row >= 0 && row < rows && col >= 0 && col < columns;
    }

    /**
     * Revela una casilla y todas las casillas vacías adyacentes recursivamente.
     * Si una casilla tiene minas adyacentes, muestra el número correspondiente y
     * detiene el revelado en esa rama.
     *
     * @param row Fila de la casilla a revelar
     * @param col Columna de la casilla a revelar
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

        box.reveal();

        if (box instanceof EmptyBox && ((EmptyBox) box).getAdjacentMinesCount() > 0) {
            return flagsRemoved;
        }

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
     * Cuenta el número de casillas marcadas con bandera en el tablero.
     * 
     * @return Número de casillas con bandera
     */
    public int getFlagCount() {
        int flagCount = 0;
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                if (boxes[row][col].isFlagged()) {
                    flagCount++;
                }
            }
        }
        return flagCount;
    }

    public void increaseFlagCount() {
        if (flagCount < totalMines) {
            flagCount++;
        } else {
            throw new IllegalStateException("No puedes colocar más banderas que el número total de minas.");
        }
    }
    
    public void decreaseFlagCount() {
        if (flagCount > 0) {
            flagCount--;
        } else {
            throw new IllegalStateException("No puedes tener un conteo de banderas negativo.");
        }
    }

    public boolean allNonMinedBoxesRevealed() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                Box box = boxes[i][j];
                if (!box.isMine() && !box.isRevealed()) {
                    return false; // Si hay una casilla no minada sin revelar, el juego no está ganado
                }
            }
        }
        return true; // Todas las casillas no minadas están reveladas
    }
    
}