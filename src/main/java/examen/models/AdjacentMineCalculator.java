package examen.models;

import examen.models.BoardInterfaces.IAdjacentMineCalculator;

import java.util.Arrays;
import java.util.stream.IntStream;


public class AdjacentMineCalculator implements IAdjacentMineCalculator {
    @Override
    public void calculateAdjacentMines(Box[][] boxes) {
        int rows = boxes.length;
        int columns = boxes[0].length;

        IntStream.range(0, rows)
                .forEach(row -> IntStream.range(0, columns)
                        .filter(col -> !(boxes[row][col] instanceof MinedBox))
                        .forEach(col -> {
                            int adjacentMineCount = countAdjacentMinesRecursive(boxes, row, col,
                                    new boolean[rows][columns]);
                            ((EmptyBox) boxes[row][col]).setAdjacentMines(adjacentMineCount);
                        }));
    }

    private int countAdjacentMinesRecursive(Box[][] boxes, int row, int col, boolean[][] visited) {
        int rows = boxes.length;
        int columns = boxes[0].length;

        if (!isValidPosition(row, col, rows, columns) || visited[row][col]) {
            return 0;
        }

        visited[row][col] = true;

        int[][] directions = {
                { -1, -1 }, { -1, 0 }, { -1, 1 },
                { 0, -1 }, { 0, 1 },
                { 1, -1 }, { 1, 0 }, { 1, 1 }
        };

        return (int) Arrays.stream(directions)
                .filter(dir -> {
                    int newRow = row + dir[0];
                    int newCol = col + dir[1];
                    return isValidPosition(newRow, newCol, rows, columns) &&
                            boxes[newRow][newCol] instanceof MinedBox;
                })
                .count();
    }

    private boolean isValidPosition(int row, int col, int rows, int columns) {
        return row >= 0 && row < rows && col >= 0 && col < columns;
    }
}
