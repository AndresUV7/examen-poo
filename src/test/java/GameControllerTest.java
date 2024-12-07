
import examen.controllers.GameController;
import examen.exceptions.GameActionException;
import examen.models.Board;
import examen.models.Box;
import examen.models.Game;
import examen.models.MinedBox;
import examen.models.Player;
import examen.repositories.GamePersistenceInterface;
import examen.repositories.GamePersistenceInterface.IGameLoadResult;
import examen.views.GameView;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class GameControllerTest {
    @Mock
    private GameView mockView;

    @Mock
    private GamePersistenceInterface mockPersistenceManager;

    private GameController gameController;
    private Game game;
    private Board board;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Create a default board
        board = Board.builder()
                .rows(5)
                .columns(5)
                .totalMines(3)
                .build();
        board.generateBoard();

        // Create a game with a player
        Player player = Player.builder().name("TestPlayer").build();
        game = Game.builder()
                .board(board)
                .player(player)
                .build();

        // Initialize GameController
        gameController = new GameController(game, mockView);
        gameController.setGamePersistenceManager(mockPersistenceManager);
    }

    @Test
    void testInitializeGameWithNoSavedGame() {
        // Simulate no saved game
        when(mockPersistenceManager.loadGameState()).thenReturn(null);

        // Mock view interactions
        when(mockView.promptPlayerName()).thenReturn("TestPlayer");
        when(mockView.promptForRows(anyString())).thenReturn(5);
        when(mockView.promptForColumns()).thenReturn(5);
        when(mockView.promptForMines(anyString(), anyInt(), anyInt())).thenReturn(3);

        // Call initialize game
        gameController.initializeGame();

        // Verify view interactions
        verify(mockView)._showWelcomeMessage();
        verify(mockView).promptPlayerName();
        verify(mockView).promptForRows("TestPlayer");
        verify(mockView).promptForColumns();
        verify(mockView).promptForMines("TestPlayer", 5, 5);

        // Verify persistence manager save
        verify(mockPersistenceManager).saveGameState(any(Game.class));
    }

    @Test
    void testLoadGameWithExistingSavedGame() {
        // Create a mock loaded game
        Game savedGame = Game.builder()
                .board(board)
                .player(Player.builder().name("SavedPlayer").build())
                .build();
        IGameLoadResult mockLoadResult = mock(IGameLoadResult.class);
        when(mockLoadResult.getGame()).thenReturn(savedGame);

        // Simulate existing saved game
        when(mockPersistenceManager.loadGameState()).thenReturn(mockLoadResult);

        // Call load game
        boolean loaded = gameController.loadGame();

        // Verify
        assertTrue(loaded);
        verify(mockView).showWelcomeMessage("SavedPlayer");
    }

    @Test
    void testStartGameWithRevealAction() {
        // Prepare mocks
        when(mockView.promptAction(anyString())).thenReturn("V");
        when(mockView.promptPosition("revelar")).thenReturn("B2");

        // Instead of trying to mock the static method, directly use the actual method
        // Or if you want to test the parsing, create a separate test for that method
        int[] coordinates = GameView.parseCoordinates("B2");
        assertEquals(1, coordinates[0]);
        assertEquals(1, coordinates[1]);

        // Capture system output
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        // Run start method in a separate thread to handle continuous loop
        Thread gameThread = new Thread(() -> {
            try {
                gameController.start();
            } catch (Exception e) {
                // Ignore any exceptions that might stop the game
            }
        });
        gameThread.start();

        // Wait a bit and interrupt the thread
        try {
            Thread.sleep(500);
            gameThread.interrupt();
        } catch (InterruptedException e) {
            // Expected
        }

        // Verify interactions
        verify(mockView).promptAction(anyString());
        verify(mockView).promptPosition("revelar");
        verify(mockPersistenceManager).saveGameState(any(Game.class));
    }

    @Test
    void testInvalidActionHandling() {
        // Prepare mocks for invalid action
        when(mockView.promptAction(anyString())).thenReturn("X");

        // Capture system output
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        // Run start method in a separate thread to handle continuous loop
        Thread gameThread = new Thread(() -> {
            try {
                gameController.start();
            } catch (Exception e) {
                // Ignore any exceptions that might stop the game
            }
        });
        gameThread.start();

        // Wait a bit and interrupt the thread
        try {
            Thread.sleep(500);
            gameThread.interrupt();
        } catch (InterruptedException e) {
            // Expected
        }

        // Verify interactions
        verify(mockView).showInvalidActionMessage();
    }

    @Test
    void testProcessPlayerMove() {
        // Prepare a box to reveal
        Box boxToReveal = board.getBoxes()[1][1];
        assertFalse(boxToReveal.isRevealed());

        // Process player move
        gameController.processPlayerMove(1, 1);

        // Verify the box is revealed
        assertTrue(boxToReveal.isRevealed());
    }

    @Test
    void testSetGamePersistenceManagerThrowsExceptionWhenNotSet() {
        // Create a new controller without setting persistence manager
        GameController controller = new GameController(game, mockView);

        // Verify that methods throw IllegalStateException
        assertThrows(IllegalStateException.class, () -> controller.initializeGame());
        assertThrows(IllegalStateException.class, () -> controller.loadGame());
    }
}