import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;

import examen.models.Board;
import examen.models.Box;
import examen.models.EmptyBox;
import examen.models.Game;
import examen.models.MinedBox;
import examen.models.Player;
import examen.repositories.GameStateManager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class GameStateManagerTest {
    private static final String TEST_GAME_STATE_FILEPATH = "src/main/resources/files/minesweeper_state.csv";
    private File testFile;

    @BeforeEach
    void setUp() throws IOException {
        // Ensure test file is clean before each test
        testFile = new File(TEST_GAME_STATE_FILEPATH);
        if (testFile.exists()) {
            testFile.delete();
        }
    }

    @AfterEach
    void tearDown() throws IOException {
        // Clean up test file after each test
        if (testFile.exists()) {
            testFile.delete();
        }
    }

    @Test
    void testSaveGameState() throws IOException, CsvException {
        // Create a mock game setup
        Player player = Player.builder().name("TestPlayer").build();
        
        Board board = Board.builder()
            .rows(5)
            .columns(5)
            .totalMines(3)
            .build();
        board.initializeEmptyBoard();

        // Add some mines to the board
        Box[][] boxes = board.getBoxes();
        boxes[1][2] = new MinedBox();
        boxes[1][2].setMine(true);
        boxes[3][4] = new MinedBox();
        boxes[3][4].setMine(true);
        boxes[0][0] = new MinedBox();
        boxes[0][0].setMine(true);

        // Flag some boxes
        boxes[1][1].setFlagged(true);
        board.increaseFlagCount();

        // Reveal some boxes
        boxes[2][3] = new EmptyBox();
        boxes[2][3].setAdjacentMines(2);
        boxes[2][3].reveal();

        Game game = Game.builder()
            .board(board)
            .player(player)
            .build();

        // Save game state
        GameStateManager.saveGameState(game);

        // Verify file was created
        assertTrue(testFile.exists());

        // Read and verify contents
        try (CSVReader reader = new CSVReader(new FileReader(testFile))) {
            List<String[]> savedState = reader.readAll();

            // Verify metadata
            assertEquals("PlayerName", savedState.get(0)[0]);
            assertEquals("TestPlayer", savedState.get(0)[1]);
            assertEquals("Rows", savedState.get(1)[0]);
            assertEquals("5", savedState.get(1)[1]);
            assertEquals("Columns", savedState.get(2)[0]);
            assertEquals("5", savedState.get(2)[1]);
            assertEquals("TotalMines", savedState.get(3)[0]);
            assertEquals("3", savedState.get(3)[1]);
        }
    }

    @Test
    void testLoadGameState() {
        // Prepare a test CSV file
        try (CSVWriter writer = new CSVWriter(new FileWriter(TEST_GAME_STATE_FILEPATH))) {
            // Write metadata
            writer.writeNext(new String[] { "PlayerName", "TestPlayer" });
            writer.writeNext(new String[] { "Rows", "5" });
            writer.writeNext(new String[] { "Columns", "5" });
            writer.writeNext(new String[] { "TotalMines", "3" });
            writer.writeNext(new String[] { "FlagCount", "1" });

            // Write mine locations
            writer.writeNext(new String[] { "MineLocation" });
            writer.writeNext(new String[] { "B", "3" });
            writer.writeNext(new String[] { "D", "5" });
            writer.writeNext(new String[] { "A", "1" });

            // Write board state
            writer.writeNext(new String[] { "Row", "1", "2", "3", "4", "5" });
            writer.writeNext(new String[] { "A", "?", "?", "?", "?", "X" });
            writer.writeNext(new String[] { "B", "?", "F", "X", "?", "?" });
            writer.writeNext(new String[] { "C", "?", "?", "2", "?", "?" });
        } catch (IOException e) {
            fail("Error creating test CSV file");
        }

        // Load game state
        GameStateManager.GameLoadResult loadResult = GameStateManager.loadGameState();

        // Verify loaded game
        assertNotNull(loadResult);
        
        Game game = loadResult.getGame();
        assertEquals("TestPlayer", game.getPlayer().getName());
        
        Board board = game.getBoard();
        assertEquals(5, board.getRows());
        assertEquals(5, board.getColumns());
        assertEquals(3, board.getTotalMines());
        
        // Verify flag count
        assertEquals(1, loadResult.getFlagCount());

        // Verify board state
        Box[][] boxes = board.getBoxes();
        
        // Check mine locations
        assertTrue(boxes[0][4] instanceof MinedBox);  // A5
        assertTrue(boxes[1][2] instanceof MinedBox);  // B3
        assertTrue(boxes[3][4] instanceof MinedBox);  // D5

        // Check flagged box
        assertTrue(boxes[1][1].isFlagged());

        // Check revealed empty box
        assertTrue(boxes[2][2].isRevealed());
        assertEquals(2, ((EmptyBox)boxes[2][2]).getAdjacentMines());
    }

    @Test
    void testLoadGameStateWithNonExistentFile() {
        // Ensure no file exists
        File file = new File(TEST_GAME_STATE_FILEPATH);
        if (file.exists()) {
            file.delete();
        }

        // Try to load game state
        GameStateManager.GameLoadResult loadResult = GameStateManager.loadGameState();

        // Verify that null is returned when no saved state exists
        assertNull(loadResult);
    }

    @Test
    void testSaveGameStateWithIOException() {
        // Create a mock game setup
        Player player = Player.builder().name("TestPlayer").build();
        Board board = Board.builder()
            .rows(5)
            .columns(5)
            .totalMines(3)
            .build();
        board.initializeEmptyBoard();

        Game game = Game.builder()
            .board(board)
            .player(player)
            .build();

        // Use try-catch to handle potential exceptions during test setup
        try {
            // Create a directory instead of a file to force an IOException
            testFile.mkdir();

            // Capture System.err output
            java.io.ByteArrayOutputStream errContent = new java.io.ByteArrayOutputStream();
            System.setErr(new java.io.PrintStream(errContent));

            // Attempt to save game state
            GameStateManager.saveGameState(game);

            // Check error message
            assertTrue(errContent.toString().contains("Error saving game state"));
        } catch (Exception e) {
            fail("Test setup failed: " + e.getMessage());
        } finally {
            // Restore standard error stream
            System.setErr(System.err);
            
            // Clean up directory
            if (testFile.exists()) {
                testFile.delete();
            }
        }
    }
}