import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import examen.models.Board;
import examen.models.Box;
import examen.models.EmptyBox;
import examen.models.MinedBox;
import examen.models.StandardBoardRenderer;
import examen.models.GameInterfaces.IBoxDisplayStrategy;


import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StandardBoardRendererTest {
    @Mock
    private IBoxDisplayStrategy mockDisplayStrategy;

    @Mock
    private Board mockBoard;

    private StandardBoardRenderer renderer;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        System.setOut(new PrintStream(outContent));
        renderer = new StandardBoardRenderer(mockDisplayStrategy);
    }

    @BeforeEach
    void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    void testPrintBoard() {
        // Arrange
        Box[][] boxes = new Box[][] {
            { mock(Box.class), mock(Box.class) },
            { mock(Box.class), mock(Box.class) }
        };
        when(mockBoard.getRows()).thenReturn(2);
        when(mockBoard.getColumns()).thenReturn(2);
        when(mockBoard.getBoxes()).thenReturn(boxes);
        
        when(mockDisplayStrategy.getRepresentation(boxes[0][0])).thenReturn("A");
        when(mockDisplayStrategy.getRepresentation(boxes[0][1])).thenReturn("B");
        when(mockDisplayStrategy.getRepresentation(boxes[1][0])).thenReturn("C");
        when(mockDisplayStrategy.getRepresentation(boxes[1][1])).thenReturn("D");

        // Act
        renderer.printBoard(mockBoard);

        // Assert
        String expectedOutput = "  1 2\nA A B\nB C D\n";
        assertEquals(expectedOutput, outContent.toString());
    }

    @Test
    void testPrintDetailedBoard() {
        // Arrange
        Box[][] boxes = new Box[][] {
            { mock(EmptyBox.class), mock(MinedBox.class) },
            { mock(EmptyBox.class), mock(Box.class) }
        };
        when(mockBoard.getRows()).thenReturn(2);
        when(mockBoard.getColumns()).thenReturn(2);
        when(mockBoard.getBoxes()).thenReturn(boxes);

        EmptyBox emptyBox1 = (EmptyBox) boxes[0][0];
        EmptyBox emptyBox2 = (EmptyBox) boxes[1][0];
        MinedBox minedBox = (MinedBox) boxes[0][1];
        Box box = boxes[1][1];

        when(emptyBox1.isRevealed()).thenReturn(true);
        when(emptyBox1.getAdjacentMinesCount()).thenReturn(1);
        when(minedBox.isRevealed()).thenReturn(true);
        when(emptyBox2.isRevealed()).thenReturn(false);
        when(emptyBox2.isFlagged()).thenReturn(false);
        when(box.isRevealed()).thenReturn(false);
        when(box.isFlagged()).thenReturn(false);

        // Act
        renderer.printDetailedBoard(mockBoard);

        // Assert
        String expectedOutput = "  1 2\nA 1 X\nB ? ?\n";
        assertEquals(expectedOutput, outContent.toString());
    }
}