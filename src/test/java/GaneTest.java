import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import examen.models.Board;
import examen.models.Box;
import examen.models.Game;
import examen.models.Player;
import examen.models.StandardBoardRenderer;
import examen.models.GameInterfaces.IBoardRenderer;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GameTest {
    @Mock
    private Board mockBoard;

    @Mock
    private Player mockPlayer;

    @Mock
    private IBoardRenderer mockRenderer;

    private Game game;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Create a Game instance with mock dependencies
        game = Game.builder()
                .board(mockBoard)
                .player(mockPlayer)
                .renderer(mockRenderer)
                .build();
    }

    @Test
    void testRevealAllBoxes() {
        // Arrange
        Box[][] boxes = new Box[][] {
            { mock(Box.class), mock(Box.class) },
            { mock(Box.class), mock(Box.class) }
        };
        when(mockBoard.getBoxes()).thenReturn(boxes);

        // Act
        game.revealAllBoxes();

        // Assert
        for (Box[] row : boxes) {
            for (Box box : row) {
                verify(box).reveal();
            }
        }
    }

    @Test
    void testRevealAdjacent() {
        // Arrange
        int row = 1;
        int col = 1;
        when(mockBoard.revealAdjacent(row, col)).thenReturn(3);

        // Act
        int revealedBoxes = game.revealAdjacent(row, col);

        // Assert
        assertEquals(3, revealedBoxes);
        verify(mockBoard).revealAdjacent(row, col);
    }

    @Test
    void testGetFlagCount() {
        // Arrange
        when(mockBoard.getFlagCount()).thenReturn(5);

        // Act
        int flagCount = game.getFlagCount();

        // Assert
        assertEquals(5, flagCount);
    }

    @Test
    void testIncreaseFlagCount() {
        // Arrange
        when(mockBoard.getFlagCount()).thenReturn(3);

        // Act
        game.increaseFlagCount();

        // Assert
        verify(mockBoard).setFlagCount(4);
    }

    @Test
    void testDecreaseFlagCount() {
        // Arrange
        when(mockBoard.getFlagCount()).thenReturn(5);

        // Act
        game.decreaseFlagCount();

        // Assert
        verify(mockBoard).setFlagCount(4);
    }

    @Test
    void testPrintBoard() {
        // Act
        game.printBoard();

        // Assert
        verify(mockRenderer).printBoard(mockBoard);
    }

    @Test
    void testPrintDetailedBoard() {
        // Act
        game.printDetailedBoard();

        // Assert
        verify(mockRenderer).printDetailedBoard(mockBoard);
    }

    @Test
    void testSetGameOver() {
        // Act & Assert
        assertThrows(UnsupportedOperationException.class, () -> {
            game.setGameOver(true);
        });
    }

    @Test
    void testBuilderDefaultRenderer() {
        // Create a new Game without specifying a renderer
        Game defaultGame = Game.builder()
                .board(mockBoard)
                .player(mockPlayer)
                .build();

        // Assert that the default renderer is of the expected type
        assertNotNull(defaultGame.getRenderer());
        assertTrue(defaultGame.getRenderer() instanceof StandardBoardRenderer);
    }
}