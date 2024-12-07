import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import examen.models.AdjacentMineCalculator;
import examen.models.Box;
import examen.models.EmptyBox;
import examen.models.MinedBox;

import static org.junit.jupiter.api.Assertions.*;

class AdjacentMineCalculatorTest {
    private AdjacentMineCalculator calculator;
    private Box[][] boxes;

    @BeforeEach
    void setUp() {
        calculator = new AdjacentMineCalculator();
        // Configurar un tablero de ejemplo para pruebas
        boxes = new Box[3][3];
        
        // Crear cajas vacías
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                boxes[row][col] = EmptyBox.builder().xPosition(row).yPosition(col).build();
            }
        }
        
        // Insertar algunas minas
        boxes[0][0] = MinedBox.builder().xPosition(0).yPosition(0).build();
        boxes[2][2] = MinedBox.builder().xPosition(2).yPosition(2).build();

        //con patron builder

    }

    @Test
    void testCalculateAdjacentMines() {
        calculator.calculateAdjacentMines(boxes);

        // Verificar el número de minas adyacentes en diferentes cajas
        assertEquals(2, ((EmptyBox)boxes[1][1]).getAdjacentMines(), 
            "La caja central debe tener 2 minas adyacentes");
        
        assertEquals(1, ((EmptyBox)boxes[0][1]).getAdjacentMines(), 
            "La caja superior central debe tener 1 mina adyacente");
        
        assertEquals(1, ((EmptyBox)boxes[1][0]).getAdjacentMines(), 
            "La caja central izquierda debe tener 1 mina adyacente");
    }

    @Test
    void testIsValidPosition() {
        // Pruebas para posiciones válidas e inválidas
        assertTrue(calculator.isValidPosition(1, 1, 3, 3), 
            "Posición dentro del tablero debe ser válida");
        
        assertFalse(calculator.isValidPosition(-1, 0, 3, 3), 
            "Posición con fila negativa no debe ser válida");
        
        assertFalse(calculator.isValidPosition(0, 3, 3, 3), 
            "Posición fuera de los límites de columna no debe ser válida");
    }

    @Test
    void testCountAdjacentMinesRecursive() {
        boolean[][] visited = new boolean[3][3];
        
        // Contar minas adyacentes en la posición central
        int adjacentMines = calculator.countAdjacentMinesRecursive(boxes, 1, 1, visited);
        
        assertEquals(2, adjacentMines, 
            "Debe contar 2 minas adyacentes en la posición central");
        
        // Reiniciar visited para la siguiente prueba
        visited = new boolean[3][3];
        
        // Contar minas adyacentes en una esquina
        adjacentMines = calculator.countAdjacentMinesRecursive(boxes, 0, 1, visited);
        
        assertEquals(1, adjacentMines, 
            "Debe contar 1 mina adyacente en la esquina superior");
    }

    @Test
    void testCalculateAdjacentMinesWithNoMines() {
        // Crear un tablero sin minas
        Box[][] emptyBoxes = new Box[3][3];
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                emptyBoxes[row][col] = EmptyBox.builder().xPosition(row).yPosition(col).build();
            }
        }

        calculator.calculateAdjacentMines(emptyBoxes);

        // Verificar que todas las cajas tienen 0 minas adyacentes
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                assertEquals(0, ((EmptyBox)emptyBoxes[row][col]).getAdjacentMines(), 
                    "Caja sin minas adyacentes debe tener 0 minas");
            }
        }
    }

    @Test
    void testCalculateAdjacentMinesWithAllMines() {
        // Crear un tablero con todas las cajas como minas
        Box[][] allMinedBoxes = new Box[3][3];
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                allMinedBoxes[row][col] = MinedBox.builder().xPosition(row).yPosition(col).build();
            }
        }

        calculator.calculateAdjacentMines(allMinedBoxes);
        
        // Este escenario no requiere verificación específica ya que las minas se omiten
    }
}