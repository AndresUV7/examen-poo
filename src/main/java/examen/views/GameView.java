package examen.views;

import java.util.Scanner;

public class GameView {
    private final Scanner scanner = new Scanner(System.in);

    public void showWelcomeMessage() {
        System.out.println("==== Bienvenido al juego de Buscaminas ====");
    }

    public void showFlagCount(int flagsUsed, int totalMines) {
        System.out.println("\nBanderas usadas: " + flagsUsed + "/" + totalMines);
    }

    public String promptAction() {
        System.out.println("¿Qué deseas hacer? (V para revelar, F para marcar/desmarcar): ");
        return scanner.nextLine().trim().toUpperCase();
    }

    public String promptPosition(String action) {
        System.out.print("Ingresa la coordenada para " + action + " (Ejemplo: B3): ");
        return scanner.nextLine();
    }

    public int promptForRows() {
        System.out.print("Ingresa el número de filas: ");
        return scanner.nextInt();
    }

    public int promptForColumns() {
        System.out.print("Ingresa el número de columnas: ");
        return scanner.nextInt();
    }

    public int promptForMines(int rows, int columns) {
        int totalMines;
        do {
            System.out.print("Ingresa el número total de minas: ");
            totalMines = scanner.nextInt();
            if (totalMines >= rows * columns) {
                System.out.println("**** Número de minas inválido. Intenta de nuevo. ****");
            }
        } while (totalMines >= rows * columns);
        scanner.nextLine(); // Consumir salto de línea
        return totalMines;
    }

    public void showInvalidActionMessage() {
        System.out.println("**** Acción inválida. Usa 'V' para revelar o 'F' para marcar/desmarcar. ****");
    }

    public void showInvalidPositionMessage() {
        System.out.println("*** Coordenada inválida. Inténtalo de nuevo. ***");
    }

    public void showAlreadyRevealedMessage() {
        System.out.println("**** Esta casilla ya está revelada. No puedes revelarla nuevamente. ****");
    }

    public void showCannotFlagRevealedMessage() {
        System.out.println("***** No puedes marcar ni desmarcar una casilla que ya está revelada. *****");
    }

    public void showFlaggedMessage() {
        System.out.println("** La casilla ha sido marcada. **");
    }

    public void showUnflaggedMessage() {
        System.out.println("** La casilla ha sido desmarcada. **");
    }

    public void showNoFlagsLeftMessage() {
        System.out.println("**** Ya has usado todas tus banderas. No puedes marcar más casillas. ****");
    }

    public void showVictoryMessage() {
        System.out.println("==== ¡Felicidades! Has ganado el juego. ====");
    }

    public void showGameOverMessage() {
        System.out.println("==== ¡Boom! Has pisado una mina. ¡Juego terminado! ====");
    }

    public void showEndGameMessage() {
        System.out.println("==== Gracias por jugar. ====");
    }

    public static int[] parseCoordinates(String position) {
        if (position.length() < 2) return null;

        char rowChar = position.toUpperCase().charAt(0);
        if (rowChar < 'A' || rowChar > 'Z') return null;

        String colString = position.substring(1);
        int column;
        try {
            column = Integer.parseInt(colString) - 1;
        } catch (NumberFormatException e) {
            return null;
        }

        int row = rowChar - 'A';
        return new int[]{row, column};
    }
}
