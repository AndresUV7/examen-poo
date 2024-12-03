package examen.models;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

// Implementa el patrón builder
@Builder
// Genera getters, setters, toString, etc.
@Data
// Genera un constructor con todos los atributos
@AllArgsConstructor
public class Board {
    private int rows;
    private int columns;
    private int totalMines;
    private Box[][] boxes;

    // Inyección de dependencia de Random para mejor capacidad de prueba

    @Builder.Default
    private final Random random = new Random();

    // Si quieres permitir inyectar un Random específico en el builder
    public Board(Integer rows, Integer columns, Integer totalMines, Box[][] boxes, Random random) {
        this.rows = rows;
        this.columns = columns;
        this.totalMines = totalMines;
        this.boxes = boxes;
        this.random = random != null ? random : new Random();
    }

    /**
     * Genera el tablero del juego con minas colocadas aleatoriamente.
     * Sigue el Principio Abierto/Cerrado permitiendo posibles sobrescrituras.
     */
    public void generateBoard() {
        // Validar entrada
        validateBoardParameters();

        // Inicializar el tablero con casilleros vacías
        initializeEmptyBoard();

        // Colocar minas aleatoriamente
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
     * Revela todas las casilleros del tablero (para depuración o final del juego).
     */
    public void revealAllBoxes() {
        Arrays.stream(boxes)
                .flatMap(Arrays::stream)
                .forEach(Box::reveal);
    }

    /**
     * Imprime el estado actual del tablero.
     * Podría extenderse para proporcionar una representación más detallada.
     */
    public void printBoard() {
        // Generar etiquetas de columnas (números)
        String columnLabels = IntStream.range(1, columns + 1)
                .mapToObj(String::valueOf)
                .collect(Collectors.joining(" ", "  ", "\n"));
        System.out.print(columnLabels);

        // Imprimir tablero con etiquetas de filas (letras)
        IntStream.range(0, rows)
                .mapToObj(row -> {
                    // Convertir cada fila a su representación con etiqueta de fila
                    String rowRepresentation = Arrays.stream(boxes[row])
                            .map(this::getBoxRepresentation)
                            .collect(Collectors.joining(" "));
                    return String.format("%c %s\n", 'A' + row, rowRepresentation);
                })
                .forEach(System.out::print);
    }

    private String getBoxRepresentation(Box box) {
        if (box.isRevealed()) {
            return box instanceof MinedBox ? "X" : String.valueOf(((EmptyBox) box).getAdjacentMinesCount());
        } else if (box.isFlagged()) {
            return "F";
        }
        return "?";
    }

    /**
     * Imprime el estado del tablero con representación de minas y casillas.
     */
    public void printBoardAux() {
        // Generar etiquetas de columnas (números)
        String columnLabels = IntStream.range(1, columns + 1)
                .mapToObj(String::valueOf)
                .collect(Collectors.joining(" ", "  ", "\n"));
        System.out.print(columnLabels);

        // Imprimir tablero con etiquetas de filas (letras)
        IntStream.range(0, rows)
                .mapToObj(row -> {
                    // Convertir cada fila a su representación detallada con etiqueta de fila
                    String rowRepresentation = Arrays.stream(boxes[row])
                            .map(this::getDetailedBoxRepresentation)
                            .collect(Collectors.joining(" "));
                    return String.format("%c %s\n", 'A' + row, rowRepresentation);
                })
                .forEach(System.out::print);
    }

    /**
     * Obtiene una representación detallada de la casilla.
     * 
     * @param box La casilla a representar
     * @return Representación de la casilla
     */
    private String getDetailedBoxRepresentation(Box box) {
        if (box.isRevealed()) {
            if (box instanceof MinedBox) {
                return "X"; // Mina revelada
            } else {
                return String.valueOf(((EmptyBox) box).getAdjacentMinesCount());
            }
        } else if (box.isFlagged()) {
            return "F"; // Casilla marcada
        }
        return "?"; // Casilla no revelada
    }

    /**
     * Calcula el número de minas adyacentes para cada casilla vacía del tablero.
     * Recorre todas las casillas y establece el conteo de minas adyacentes para las
     * casillas no minadas.
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
    private boolean isValidPosition(int row, int col) {
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
    public void revealAdjacent(int row, int col) {
        if (!isValidPosition(row, col))
            return; // Fuera de los límites del tablero
        Box box = boxes[row][col];

        // Si ya está revelada o marcada con una bandera, no hacer nada
        if (box.isRevealed() || box.isFlagged())
            return;

        box.reveal(); // Revelar la casilla actual

        // Si es una casilla vacía con minas adyacentes, detener el revelado aquí
        if (box instanceof EmptyBox && ((EmptyBox) box).getAdjacentMinesCount() > 0) {
            return;
        }

        // Si es una casilla vacía sin minas adyacentes, continuar con el revelado en
        // las 8 direcciones
        int[][] directions = {
                { -1, -1 }, { -1, 0 }, { -1, 1 },
                { 0, -1 }, { 0, 1 },
                { 1, -1 }, { 1, 0 }, { 1, 1 }
        };

        for (int[] direction : directions) {
            int newRow = row + direction[0];
            int newCol = col + direction[1];
            revealAdjacent(newRow, newCol); // Llamada recursiva
        }
    }

}