package examen;

import java.util.Scanner;

import examen.models.Board;
import examen.models.Box;
import examen.models.MinedBox;
import examen.repositories.GameStateManager;

public class MinesweeperGame {

    public static void main(String[] args) {
        System.out.println("==== Bienvenido al juego de Buscaminas ====");
        Scanner scanner = new Scanner(System.in);
        boolean gameOver = false;

        // Intentar cargar estado guardado
        GameStateManager.BoardLoadResult loadedGame = GameStateManager.loadGameState();

        Board gameBoard;
        int flagCount;
        if (loadedGame != null) {
            // Cargar estado guardado
            gameBoard = loadedGame.getBoard();
            flagCount = loadedGame.getFlagCount();
            System.out.println("==== Juego anterior cargado. ====");
        } else {
            // Pedir al usuario las dimensiones y la cantidad de minas
            System.out.println("Configura tu tablero:");
            System.out.print("Ingresa el número de filas: ");
            int rows = scanner.nextInt();

            System.out.print("Ingresa el número de columnas: ");
            int columns = scanner.nextInt();

            System.out.print("Ingresa el número total de minas: ");
            int totalMines = scanner.nextInt();

            // Validar las entradas
            while (totalMines >= rows * columns) {
                System.out.println(
                        "**** El número de minas no puede ser igual o mayor al total de casillas. Intenta de nuevo.****");
                System.out.print("Ingresa el número total de minas: ");
                totalMines = scanner.nextInt();
            }
            scanner.nextLine(); // Consumir el salto de línea pendiente

            // Crear el tablero con las configuraciones dadas
            gameBoard = Board.builder()
                    .rows(rows)
                    .columns(columns)
                    .totalMines(totalMines)
                    .build();
            gameBoard.generateBoard();
            flagCount = 0;
            GameStateManager.saveGameState(gameBoard, flagCount);
        }
        gameBoard.printBoard();

        while (!gameOver) {
            System.out.println("\nBanderas usadas: " + flagCount + "/" + gameBoard.getTotalMines());
            System.out.println("¿Qué deseas hacer? (V para revelar, F para marcar/desmarcar): ");
            String action = scanner.nextLine().trim().toUpperCase();

            switch (action) {
                case "V": // Revelar casilla
                System.out.print("Ingresa la coordenada (Ejemplo: B3): ");
                String positionToReveal = scanner.nextLine();
                int[] coordsToReveal = parseCoordinates(positionToReveal);
            
                if (coordsToReveal == null) {
                    System.out.println("*** Coordenada inválida. Inténtalo de nuevo. ***");
                    break;
                }
            
                int revealRow = coordsToReveal[0];
                int revealCol = coordsToReveal[1];
            
                if (gameBoard.getBoxes()[revealRow][revealCol].isRevealed()) {
                    System.out.println("**** Esta casilla ya está revelada. No puedes revelarla nuevamente. ****");
                    gameBoard.printBoard();
                } else {
                    if (gameBoard.getBoxes()[revealRow][revealCol] instanceof MinedBox) {
                        System.out.println("==== ¡Boom! Has pisado una mina. ¡Juego terminado! ====");
                        gameBoard.revealAllBoxes();
                        gameBoard.printBoard();
                        gameOver = true;
                        GameStateManager.clearGameState();
                        break;
                    } else {
                        int removedFlags = gameBoard.revealAdjacent(revealRow, revealCol);
                        flagCount -= removedFlags;
                        gameBoard.printBoard();
                        GameStateManager.saveGameState(gameBoard, flagCount);
                    }
            
                    if (isGameWon(gameBoard)) {
                        System.out.println("==== ¡Felicidades! Has ganado el juego. ====");
                        gameBoard.revealAllBoxes();
                        gameBoard.printBoard();
                        gameOver = true;
                        GameStateManager.clearGameState();
                    }
                }
                break;

                case "F": // Marcar o desmarcar casilla
                    System.out.print("Ingresa la coordenada para marcar/desmarcar (Ejemplo: B3): ");
                    String positionToFlag = scanner.nextLine();
                    int[] coordsToFlag = parseCoordinates(positionToFlag);

                    if (coordsToFlag == null) {
                        System.out.println("*** Coordenada inválida. Inténtalo de nuevo. ***");
                        break;
                    }

                    int flagRow = coordsToFlag[0];
                    int flagCol = coordsToFlag[1];

                    if (gameBoard.getBoxes()[flagRow][flagCol].isRevealed()) {
                        System.out.println("***** No puedes marcar ni desmarcar una casilla que ya está revelada. *****");
                    } else {
                        boolean isFlagged = gameBoard.getBoxes()[flagRow][flagCol].isFlagged();
                        if (isFlagged) {
                            // Si la casilla está con bandera (quitar la bandera)
                            gameBoard.getBoxes()[flagRow][flagCol].setFlagged(false);
                            flagCount--;
                            System.out.println("** La casilla ha sido desmarcada.**");
                        } else {
                            // Verificar si se puede añadir una nueva bandera
                            if (flagCount < gameBoard.getTotalMines()) {
                                gameBoard.getBoxes()[flagRow][flagCol].setFlagged(true);
                                flagCount++;
                                System.out.println("** La casilla ha sido marcada. **");
                            } else {
                                System.out.println("**** Ya has usado todas tus banderas. No puedes marcar más casillas. ****");
                            }
                        }
                        gameBoard.printBoard(); // Mostrar el tablero con la casilla marcada/desmarcada
                        GameStateManager.saveGameState(gameBoard, flagCount); // Guardar estado
                    }
                    break;

                default:
                    System.out.println("**** Acción inválida. Usa 'V' para revelar o 'F' para marcar/desmarcar. ****");
                    break;
            }
        }

        System.out.println("==== Gracias por jugar. ====");
        scanner.close();
    }

    /**
     * Verifica si el usuario ha ganado el juego.
     *
     * @param board El tablero del juego
     * @return true si todas las minas están marcadas y todas las casillas no
     *         minadas están reveladas
     */
    private static boolean isGameWon(Board board) {
        for (Box[] row : board.getBoxes()) {
            for (Box box : row) {
                // Si hay una casilla sin mina que no está revelada, el juego no está ganado
                if (!box.isMine() && !box.isRevealed()) {
                    return false;
                }
            }
        }
        return true; // Todas las casillas sin minas están reveladas
    }

    /**
     * Convierte una coordenada ingresada por el usuario (Ej: "B3") a índices del
     * tablero.
     *
     * @param position Coordenada ingresada
     * @return Un arreglo con las coordenadas [fila, columna] o null si es inválida
     */
    private static int[] parseCoordinates(String position) {
        if (position.length() < 2)
            return null;

        char rowChar = position.toUpperCase().charAt(0);
        if (rowChar < 'A' || rowChar > 'Z')
            return null;

        String colString = position.substring(1);
        int column;
        try {
            column = Integer.parseInt(colString) - 1; // Convertir columna a índice
        } catch (NumberFormatException e) {
            return null;
        }

        int row = rowChar - 'A'; // Convertir fila a índice
        return new int[] { row, column };
    }
}