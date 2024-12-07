import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import examen.models.Box;
import lombok.experimental.SuperBuilder;

import static org.junit.jupiter.api.Assertions.*;

@SuperBuilder
// Concrete implementation of Box for testing
class TestBox extends Box {
    @Override
    public boolean reveal() {
        // Simple implementation for testing
        if (!isMine() && !isFlagged()) {
            setRevealed(true);
            return true;
        }
        return false;
    }
}

class BoxTest {
    private TestBox box;

    @BeforeEach
    void setUp() {
        // Using Builder pattern to create Box instances for testing
        box = TestBox.builder()
            .xPosition(5)
            .yPosition(5)
            .isMine(false)
            .isRevealed(false)
            .isFlagged(false)
            .adjacentMines(2)
            .build();
    }

    @Test
    void testBoxCreation() {
        // Test that the builder creates a box with correct initial state
        assertEquals(5, box.getXPosition());
        assertEquals(5, box.getYPosition());
        assertFalse(box.isMine());
        assertFalse(box.isRevealed());
        assertFalse(box.isFlagged());
        assertEquals(2, box.getAdjacentMines());
    }

    @Test
    void testIsSafe() {
        // Test isSafe method
        assertFalse(box.isMine());
        assertTrue(box.isSafe());

        // Create a mine box
        TestBox mineBox = TestBox.builder()
            .isMine(true)
            .build();
        
        assertFalse(mineBox.isSafe());
    }

    @Test
    void testReveal() {
        // Test reveal method for a safe, unflagged box
        assertTrue(box.reveal());
        assertTrue(box.isRevealed());

        // Reset the box
        box = TestBox.builder()
            .isMine(true)
            .build();

        // Test reveal for a mine box
        assertFalse(box.reveal());
    }

    @Test
    void testFlaggedBoxCannotBeRevealed() {
        // Flag the box
        box.setFlagged(true);

        // Try to reveal a flagged box
        assertFalse(box.reveal());
        assertFalse(box.isRevealed());
    }

    @Test
    void testMultipleReveals() {
        // First reveal should work
        assertTrue(box.reveal());
        assertTrue(box.isRevealed());

        // Second reveal should not change state
        assertFalse(box.reveal());
        assertTrue(box.isRevealed());
    }

    @Test
    void testPositionSetters() {
        // Test position setters
        box.setXPosition(10);
        box.setYPosition(20);

        assertEquals(10, box.getXPosition());
        assertEquals(20, box.getYPosition());
    }

    @Test
    void testAdjacentMinesSetter() {
        // Test adjacent mines setter
        box.setAdjacentMines(5);
        assertEquals(5, box.getAdjacentMines());
    }
}