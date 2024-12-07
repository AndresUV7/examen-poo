import examen.views.GameView;
import examen.exceptions.BoardException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class GameViewTest {
    private GameView gameView;
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final ByteArrayInputStream testIn;
    private final Scanner scanner;

    public GameViewTest() {
        // Default constructor with a dummy input
        testIn = new ByteArrayInputStream("".getBytes());
        scanner = new Scanner(testIn);
    }

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outputStream));
    }

    @AfterEach
    void restoreStreams() {
        System.setOut(originalOut);
    }

    private GameView createGameViewWithInput(String input) {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(input.getBytes());
        Scanner scannerWithInput = new Scanner(inputStream);
        return new GameView(scannerWithInput);
    }

    @Test
    void testPromptAction_ValidInput() {
        gameView = createGameViewWithInput("V\n");
        String action = gameView.promptAction("Player1");
        assertEquals("V", action);
    }

    @Test
    void testPromptPosition_ValidInput() {
        gameView = createGameViewWithInput("B3\n");
        String position = gameView.promptPosition("revelar");
        assertEquals("B3", position);
    }

    @Test
    void testPromptForRows_ValidInput() {
        gameView = createGameViewWithInput("5\n");
        int rows = gameView.promptForRows("Player1");
        assertEquals(5, rows);
    }

    @Test
    void testPromptForRows_InvalidInput() {
        gameView = createGameViewWithInput("-1\nabc\n5\n");
        int rows = gameView.promptForRows("Player1");
        assertEquals(5, rows);
        assertTrue(outputStream.toString().contains("Error: El número de filas debe ser un número positivo"));
    }

    @Test
    void testPromptForColumns_ValidInput() {
        gameView = createGameViewWithInput("8\n");
        int columns = gameView.promptForColumns();
        assertEquals(8, columns);
    }

    @Test
    void testPromptForMines_ValidInput() {
        gameView = createGameViewWithInput("10\n");
        int mines = gameView.promptForMines("Player1", 10, 10);
        assertEquals(10, mines);
    }

    @Test
    void testPromptForMines_TooManyMines() {
        gameView = createGameViewWithInput("100\n10\n");
        int mines = gameView.promptForMines("Player1", 10, 10);
        assertEquals(10, mines);
        assertTrue(outputStream.toString().contains("Número de minas inválido"));
    }

    @Test
    void testShowErrorMessage() {
        gameView = new GameView(scanner);
        gameView.showErrorMessage("Test Error");
        assertTrue(outputStream.toString().contains("ERROR: Test Error"));
    }

    @Test
    void testShowWelcomeMessage() {
        gameView = new GameView(scanner);
        gameView.showWelcomeMessage("Player1");
        assertTrue(outputStream.toString().contains("Bienvenido de vuelta al juego de Buscaminas"));
    }

    @Test
    void testShowVictoryMessage() {
        gameView = new GameView(scanner);
        gameView.showVictoryMessage("Player1");
        assertTrue(outputStream.toString().contains("¡Felicidades, Player1! Has ganado el juego"));
    }

    @Test
    void testShowGameOverMessage() {
        gameView = new GameView(scanner);
        gameView.showGameOverMessage("Player1");
        assertTrue(outputStream.toString().contains("¡BOOM!"));
        assertTrue(outputStream.toString().contains("Player1"));
    }

    @Test
    void testShowInvalidActionMessage() {
        gameView = new GameView(scanner);
        gameView.showInvalidActionMessage();
        assertTrue(outputStream.toString().contains("Acción inválida"));
    }
}