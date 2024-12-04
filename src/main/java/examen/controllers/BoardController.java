package examen.controllers;

import examen.models.Board;
import examen.models.Box;
import examen.models.EmptyBox;
import examen.models.MinedBox;

import java.util.Scanner;

public class BoardController {
    private final Board board;

    public BoardController(int rows, int columns, int totalMines) {
        this.board = Board.builder()
                .rows(rows)
                .columns(columns)
                .totalMines(totalMines)
                .build();
        this.board.generateBoard();
    }

    // Método público para permitir acceso desde la vista
    public Board getBoard() {
        return board;
    }

    /**
     * Inicia el juego y gestiona las interacciones del jugador.
     */
    public void startGame() {
        Scanner scanner = new Scanner(System.in);
        boolean gameRunning = true;

        System.out.println("Bienvenido al Buscaminas!");
        board.printBoard();

        while (gameRunning) {
            System.out.print("Ingrese su movimiento (Ejemplo: 'A1' o 'marcar B3'): ");
            String input = scanner.nextLine().trim();

            try {
                if (input.startsWith("marcar")) {
                    String[] parts = input.split(" ");
                    String position = parts[1];
                    toggleFlag(position);
                } else {
                    revealBox(input);
                }

                board.printBoard();
                if (isGameWon()) {
                    System.out.println("===== ¡Felicidades! Has ganado el juego. =====");
                    gameRunning = false;
                }
            } catch (IllegalArgumentException e) {
                System.out.println("***** Movimiento inválido: *****" + e.getMessage());
            } catch (IndexOutOfBoundsException e) {
                System.out.println("***** Posición fuera del rango del tablero.*****");
            } catch (Exception e) {
                System.out.println("** Error: **" + e.getMessage());
            }
        }

        scanner.close();
    }

    /**
     * Revela una casilla en el tablero.
     *
     * @param position La posición a revelar (Ejemplo: "A1").
     */
    public void revealBox(String position) {
        int[] coordinates = parsePosition(position);
        int row = coordinates[0];
        int col = coordinates[1];

        Box box = board.getBoxes()[row][col];
        if (box instanceof MinedBox) {
            box.reveal();
            System.out.println("===== ¡Boom! Has perdido el juego. =====");
            board.revealAllBoxes();
            board.printBoard();
            System.exit(0);
        } else if (box instanceof EmptyBox) {
            board.revealAdjacent(row, col);
        }
    }

    /**
     * Alterna la marca de una casilla con una bandera.
     *
     * @param position La posición a marcar (Ejemplo: "A1").
     */
    public void toggleFlag(String position) {
        int[] coordinates = parsePosition(position);
        int row = coordinates[0];
        int col = coordinates[1];

        Box box = board.getBoxes()[row][col];
        box.setFlagged(!box.isFlagged());
    }

    /**
     * Verifica si todas las casillas seguras han sido reveladas.
     *
     * @return true si el jugador ha ganado, false de lo contrario.
     */
    public boolean isGameWon() {
        for (Box[] row : board.getBoxes()) {
            for (Box box : row) {
                if (!box.isMine() && !box.isRevealed()) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Convierte una posición en formato de texto (Ejemplo: "A1") a coordenadas.
     *
     * @param position La posición a convertir.
     * @return Un arreglo con las coordenadas [fila, columna].
     * @throws IllegalArgumentException si el formato es inválido.
     */
    private int[] parsePosition(String position) {
        if (position.length() < 2) {
            throw new IllegalArgumentException("*** Formato inválido. Use una letra y un número, como 'A1'. ***");
        }

        char rowChar = position.toUpperCase().charAt(0);
        int row = rowChar - 'A';
        int col = Integer.parseInt(position.substring(1)) - 1;

        if (row < 0 || row >= board.getRows() || col < 0 || col >= board.getColumns()) {
            throw new IndexOutOfBoundsException("*** Posición fuera de rango. ***");
        }

        return new int[]{row, col};
    }
}
