package examen.models;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.util.Random;
import java.util.stream.IntStream;

import examen.models.BoardInterfaces.IAdjacentMineCalculator;
import examen.models.BoardInterfaces.IBoardGenerator;
import examen.models.BoardInterfaces.IBoardValidator;
import examen.models.BoardInterfaces.IMineStrategy;

@Builder
@Data
@AllArgsConstructor
public class Board implements IBoardGenerator {
    private int rows;
    private int columns;
    private int totalMines;
    private Box[][] boxes;

    @Builder.Default
    private final Random random = new Random();

    @Builder.Default
    private int flagCount = 0;

    @Builder.Default
    private IBoardValidator boardValidator = new StandardBoardValidator();

    @Builder.Default
    private IMineStrategy mineStrategy = new RandomMinePlacer();

    @Builder.Default
    private IAdjacentMineCalculator adjacentMineCalculator = new AdjacentMineCalculator();

    @Override
    public void generate(Box[][] boxes, int totalMines) {
        boardValidator.validate(rows, columns, totalMines);
        initializeEmptyBoard();
        mineStrategy.placeMines(this.boxes, totalMines, random);
        adjacentMineCalculator.calculateAdjacentMines(this.boxes);
    }

    public void generateBoard() {
        generate(boxes, totalMines);
    }

    public void initializeEmptyBoard() {
        boxes = IntStream.range(0, rows)
                .mapToObj(i -> IntStream.range(0, columns)
                        .mapToObj(j -> {
                            EmptyBox emptyBox = new EmptyBox();
                            emptyBox.setXPosition(i);
                            emptyBox.setYPosition(j);
                            return emptyBox;
                        })
                        .toArray(Box[]::new))
                .toArray(Box[][]::new);
    }

    public boolean isValidPosition(int row, int col) {
        return row >= 0 && row < rows && col >= 0 && col < columns;
    }

    public int revealAdjacent(int row, int col) {
        if (!isValidPosition(row, col))
            return 0;

        Box box = boxes[row][col];

        if (box.isRevealed())
            return 0;

        int flagsRemoved = 0;
        if (box.isFlagged()) {
            box.setFlagged(false);
            flagsRemoved++;
        }

        box.reveal();

        if (box instanceof EmptyBox && ((EmptyBox) box).getAdjacentMinesCount() > 0) {
            return flagsRemoved;
        }

        int[][] directions = {
                { -1, -1 }, { -1, 0 }, { -1, 1 },
                { 0, -1 }, { 0, 1 },
                { 1, -1 }, { 1, 0 }, { 1, 1 }
        };

        for (int[] direction : directions) {
            int newRow = row + direction[0];
            int newCol = col + direction[1];
            flagsRemoved += revealAdjacent(newRow, newCol);
        }

        return flagsRemoved;
    }

    public int getFlagCount() {
        return (int) IntStream.range(0, rows)
                .flatMap(row -> IntStream.range(0, columns)
                        .filter(col -> boxes[row][col].isFlagged()))
                .count();
    }

    public void increaseFlagCount() {
        if (flagCount < totalMines) {
            flagCount++;
        } else {
            throw new IllegalStateException("Cannot place more flags than total mines.");
        }
    }
    
    public void decreaseFlagCount() {
        if (flagCount > 0) {
            flagCount--;
        } else {
            throw new IllegalStateException("Flag count cannot be negative.");
        }
    }

    public boolean allNonMinedBoxesRevealed() {
        return IntStream.range(0, rows)
                .flatMap(i -> IntStream.range(0, columns)
                        .filter(j -> !boxes[i][j].isMine() && !boxes[i][j].isRevealed()))
                .count() == 0;
    }
}