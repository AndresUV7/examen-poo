import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import examen.models.Board;
import examen.models.EmptyBox;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import examen.models.BoardInterfaces.*;


class BoardTest {
    private Board board;
    private IBoardValidator mockBoardValidator;
    private IMineStrategy mockMineStrategy;
    private IAdjacentMineCalculator mockAdjacentMineCalculator;

    @BeforeEach
    void setUp() {
        // Configuración inicial con mocks para dependencias
        mockBoardValidator = mock(IBoardValidator.class);
        mockMineStrategy = mock(IMineStrategy.class);
        mockAdjacentMineCalculator = mock(IAdjacentMineCalculator.class);

        // Usar el constructor Builder sin pasar Random directamente
        board = Board.builder()
                .rows(10)
                .columns(10)
                .totalMines(20)
                .boardValidator(mockBoardValidator)
                .mineStrategy(mockMineStrategy)
                .adjacentMineCalculator(mockAdjacentMineCalculator)
                .build();
    }

    @Test
    void testGenerateBoard() {
        // Configurar comportamiento de mocks
        doNothing().when(mockBoardValidator).validate(anyInt(), anyInt(), anyInt());
        doNothing().when(mockMineStrategy).placeMines(any(), anyInt(), any());
        doNothing().when(mockAdjacentMineCalculator).calculateAdjacentMines(any());

        // Ejecutar generación del tablero
        board.generateBoard();

        // Verificar llamadas a métodos
        verify(mockBoardValidator).validate(10, 10, 20);
        verify(mockMineStrategy).placeMines(eq(board.getBoxes()), eq(20), any());
        verify(mockAdjacentMineCalculator).calculateAdjacentMines(eq(board.getBoxes()));
    }

    @Test
    void testInitializeEmptyBoard() {
        board.initializeEmptyBoard();

        // Verificar que se ha creado un tablero vacío con las dimensiones correctas
        assertNotNull(board.getBoxes());
        assertEquals(10, board.getBoxes().length);
        assertEquals(10, board.getBoxes()[0].length);

        // Verificar que todas las cajas son EmptyBox y tienen posiciones correctas
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                assertTrue(board.getBoxes()[i][j] instanceof EmptyBox);
                assertEquals(i, board.getBoxes()[i][j].getXPosition());
                assertEquals(j, board.getBoxes()[i][j].getYPosition());
            }
        }
    }

    @Test
    void testIsValidPosition() {
        // Posiciones válidas
        assertTrue(board.isValidPosition(0, 0));
        assertTrue(board.isValidPosition(9, 9));

        // Posiciones inválidas
        assertFalse(board.isValidPosition(-1, 0));
        assertFalse(board.isValidPosition(0, -1));
        assertFalse(board.isValidPosition(10, 0));
        assertFalse(board.isValidPosition(0, 10));
    }

    @Test
    void testRevealAdjacent() {
        // Configurar un tablero para pruebas de revelado
        board.initializeEmptyBoard();
        board.getBoxes()[5][5] = new EmptyBox();
        board.getBoxes()[5][5].setFlagged(true);

        int flagsRemoved = board.revealAdjacent(5, 5);

        // Verificar que la caja ha sido revelada y desflajeada
        assertTrue(board.getBoxes()[5][5].isRevealed());
        assertFalse(board.getBoxes()[5][5].isFlagged());
        assertEquals(1, flagsRemoved);
    }

    @Test
    void testGetFlagCount() {
        board.initializeEmptyBoard();
        
        // Marcar algunas cajas como marcadas
        board.getBoxes()[1][1].setFlagged(true);
        board.getBoxes()[2][2].setFlagged(true);
        board.getBoxes()[3][3].setFlagged(true);

        assertEquals(3, board.getFlagCount());
    }

    @Test
    void testIncreaseFlagCount() {
        board.initializeEmptyBoard();
        board.setFlagCount(10);
        
        // Aumentar contador de banderas
        board.increaseFlagCount();
        assertEquals(11, board.getFlagCount());

        // Intentar sobrepasar el límite de minas
        board.setFlagCount(20);
        assertThrows(IllegalStateException.class, () -> board.increaseFlagCount());
    }

    @Test
    void testDecreaseFlagCount() {
        board.initializeEmptyBoard();
        board.setFlagCount(5);
        
        // Disminuir contador de banderas
        board.decreaseFlagCount();
        assertEquals(4, board.getFlagCount());

        // Intentar disminuir por debajo de 0
        board.setFlagCount(0);
        assertThrows(IllegalStateException.class, () -> board.decreaseFlagCount());
    }

    @Test
    void testAllNonMinedBoxesRevealed() {
        board.initializeEmptyBoard();
        
        // Revelar todas las cajas que no son minas
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                board.getBoxes()[i][j].reveal();
            }
        }

        assertTrue(board.allNonMinedBoxesRevealed());

        // Volver a establecer una caja sin revelar
        board.getBoxes()[5][5].setRevealed(false);
        assertFalse(board.allNonMinedBoxesRevealed());
    }
}