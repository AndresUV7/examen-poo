package examen.views;

import examen.controllers.BoardController;
import examen.models.Board;

import java.util.Scanner;

public class GameView {
    private final BoardController controller;
    private final Scanner scanner;

    public GameView(BoardController controller) {
        this.controller = controller;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        System.out.println("Bienvenido al juego de Buscaminas!");
        boolean gameRunning = true;

        while (gameRunning) {
            displayBoard();
            System.out.print("Ingrese su movimiento (Ejemplo: 'A1' para revelar o 'marcar B3' para marcar una casilla): ");
            String input = scanner.nextLine().trim();

            try {
                if (input.startsWith("marcar")) {
                    String[] parts = input.split(" ");
                    if (parts.length == 2) {
                        controller.toggleFlag(parts[1]);
                    } else {
                        System.out.println("Formato de comando inválido. Use 'marcar A1'.");
                        continue;
                    }
                } else {
                    controller.revealBox(input);
                }

                if (controller.isGameWon()) {
                    displayBoard();
                    System.out.println("¡Felicidades! Has ganado el juego.");
                    gameRunning = false;
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (IndexOutOfBoundsException e) {
                System.out.println("Posición fuera de los límites del tablero. Intente de nuevo.");
            } catch (Exception e) {
                System.out.println("Error inesperado: " + e.getMessage());
            }
        }

        scanner.close();
    }

    public void displayBoard() {
        Board board = controller.getBoard();
        System.out.println("Estado actual del tablero:");
        board.printBoard(); // Imprime el tablero en la consola
    }
}
