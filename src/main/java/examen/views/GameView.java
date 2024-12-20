package examen.views;

import java.util.Scanner;

import examen.views.ViewInterfaces.InputView;
import examen.views.ViewInterfaces.MessageView;
import examen.exceptions.BoardException;

public class GameView implements MessageView, InputView {
    private final Scanner scanner;

    // Inyección de dependencia para facilitar pruebas
    public GameView(Scanner scanner) {
        this.scanner = scanner;
    }

    // Validación de excepciones GameControllerException
    public void showErrorMessage(String message) {
        System.out.println("ERROR: " + message);
    }

    // Implementación de los métodos de InputView
    @Override
    public String promptAction(String playerName) {
        System.out.println("¿Qué deseas hacer " + playerName + "? (V para revelar, F para marcar/desmarcar): ");
        return scanner.nextLine().trim().toUpperCase();
    }

    @Override
    public String promptPosition(String action) {
        System.out.print("Ingresa la coordenada para " + action + " (Ejemplo: B3): ");
        return scanner.nextLine();
    }

    @Override
    public int promptForRows(String playerName) {
        while (true) {
            try {
                System.out.print(playerName + ", ingresa el número de filas: ");
                int rows = validarNumeroPositivo(scanner.nextLine(), "filas");
                return rows;
            } catch (BoardException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    @Override
    public int promptForColumns() {
        while (true) {
            try {
                System.out.print("Ingresa el número de columnas: ");
                int columns = validarNumeroPositivo(scanner.nextLine(), "columnas");
                return columns;
            } catch (BoardException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    @Override
    public int promptForMines(String playerName, int rows, int columns) {
        while (true) {
            try {
                System.out.print("Ingresa el número total de minas: ");
                int totalMines = validarNumeroPositivo(scanner.nextLine(), "minas");

                if (totalMines >= rows * columns) {
                    System.out.println("**** Número de minas inválido. Intenta de nuevo " + playerName + ". ****");
                    continue;
                }

                return totalMines;
            } catch (BoardException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    // Método de validación
    private int validarNumeroPositivo(String entrada, String tipoNumero) {
        try {
            int numero = Integer.parseInt(entrada);

            if (numero <= 0) {
                throw new BoardException("Error: El número de " + tipoNumero + " debe ser un número positivo.");
            }

            return numero;
        } catch (NumberFormatException e) {
            throw new BoardException("Error: Por favor, ingrese un número válido para " + tipoNumero + ".");
        }
    }

    @Override
    public String promptPlayerName() {
        System.out.print("Ingresa tu nombre: ");
        return scanner.nextLine().trim();
    }

    // Implementación de los métodos de MessageView
    @Override
    public void showWelcomeMessage(String playerName) {
        System.out.println("==== " + playerName + ", Bienvenido de vuelta al juego de Buscaminas ====");
    }

    @Override
    public void showVictoryMessage(String playerName) {
        System.out.println("==== ¡Felicidades, " + playerName + "! Has ganado el juego. ====");
    }

    @Override
    public void showGameOverMessage(String playerName) {
        System.out.println("¡BOOM!" + System.lineSeparator() + " ¡JUEGO TERMINADO HAS SALIDO VOLANDO! " + playerName);
    }

    @Override
    public void showEndGameMessage(String playerName) {
        System.out.println("==== Gracias por jugar, " + playerName + ". ====");
    }

    public void _showWelcomeMessage() {
        System.out.println("==== Bienvenido al juego de Buscaminas ====");
    }

    @Override
    public void showFlagCount(int flagsUsed, int totalMines) {
        System.out.println("\nBanderas usadas: " + flagsUsed + "/" + totalMines);
    }

    @Override
    public void showInvalidActionMessage() {
        System.out.println("**** Acción inválida. Usa 'V' para revelar o 'F' para marcar/desmarcar. ****");
    }

    @Override
    public void showInvalidPositionMessage() {
        System.out.println("**** MOVIMIENTO INVALIDO ****" + System.lineSeparator()
                + "**** LA COORDENADA INGRESADA NO EXISTE. ****");
    }

    @Override
    public void showAlreadyRevealedMessage() {
        System.out.println(
                "**** MOVIMIENTO INVALIDO ****" + System.lineSeparator() + "**** LA CELDA YA ESTÁ REVELADA. ****");
    }

    @Override
    public void showCannotFlagRevealedMessage() {
        System.out.println("***** No puedes marcar ni desmarcar una casilla que ya está revelada. *****");
    }

    @Override
    public void showFlaggedMessage(String playerName) {
        System.out.println("** La casilla ha sido marcada. **");
    }

    @Override
    public void showUnflaggedMessage(String playerName) {
        System.out.println("** La casilla ha sido desmarcada. **");
    }

    @Override
    public void showNoFlagsLeftMessage() {
        System.out.println("**** Ya has usado todas tus banderas. No puedes marcar más casillas. ****");
    }

    // Método parseCoordinates añadido
    public static int[] parseCoordinates(String position) {
        if (position.length() < 2)
            return null;

        char rowChar = position.toUpperCase().charAt(0);
        if (rowChar < 'A' || rowChar > 'Z')
            return null;

        String colString = position.substring(1);
        int column;
        try {
            column = Integer.parseInt(colString) - 1;
        } catch (NumberFormatException e) {
            return null;
        }

        int row = rowChar - 'A';
        return new int[] { row, column };
    }
}
