import org.junit.jupiter.api.Test;

import examen.models.Box;
import examen.models.EmptyBox;
import examen.models.MinedBox;
import examen.models.RandomMinePlacer;

import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;

class RandomMineplacerTest {
    @Test
    void testPlaceMines() {
        // Arrange
        Box[][] boxes = new Box[3][3];
        for (int i = 0; i < boxes.length; i++) {
            for (int j = 0; j < boxes[i].length; j++) {
                boxes[i][j] = new EmptyBox();
            }
        }
        
        Random fixedRandom = new Random(42); // Seed for reproducibility
        RandomMinePlacer minePlacer = new RandomMinePlacer();
        int totalMines = 5;

        // Act
        minePlacer.placeMines(boxes, totalMines, fixedRandom);

        // Assert
        int minedBoxCount = countMinedBoxes(boxes);
        assertEquals(totalMines, minedBoxCount);
    }

    @Test
    void testPlaceMinesTotalMineLimitRespected() {
        // Arrange
        Box[][] boxes = new Box[2][2];
        for (int i = 0; i < boxes.length; i++) {
            for (int j = 0; j < boxes[i].length; j++) {
                boxes[i][j] = new EmptyBox();
            }
        }
        
        Random fixedRandom = new Random(42);
        RandomMinePlacer minePlacer = new RandomMinePlacer();
        int totalMines = 4; // More mines than available boxes

        // Act
        minePlacer.placeMines(boxes, totalMines, fixedRandom);

        // Assert
        int minedBoxCount = countMinedBoxes(boxes);
        assertEquals(4, minedBoxCount);
    }

    @Test
    void testPlacedMinesHaveCorrectProperties() {
        // Arrange
        Box[][] boxes = new Box[3][3];
        for (int i = 0; i < boxes.length; i++) {
            for (int j = 0; j < boxes[i].length; j++) {
                boxes[i][j] = new EmptyBox();
            }
        }
        
        Random fixedRandom = new Random(42);
        RandomMinePlacer minePlacer = new RandomMinePlacer();
        int totalMines = 3;

        // Act
        minePlacer.placeMines(boxes, totalMines, fixedRandom);

        // Assert
        for (int i = 0; i < boxes.length; i++) {
            for (int j = 0; j < boxes[i].length; j++) {
                if (boxes[i][j] instanceof MinedBox) {
                    MinedBox minedBox = (MinedBox) boxes[i][j];
                    assertTrue(minedBox.isMine());
                    assertEquals(i, minedBox.getXPosition());
                    assertEquals(j, minedBox.getYPosition());
                }
            }
        }
    }

    private int countMinedBoxes(Box[][] boxes) {
        int count = 0;
        for (Box[] row : boxes) {
            for (Box box : row) {
                if (box instanceof MinedBox) {
                    count++;
                }
            }
        }
        return count;
    }
}