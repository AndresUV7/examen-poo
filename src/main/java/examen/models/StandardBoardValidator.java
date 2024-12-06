package examen.models;

import examen.models.BoardInterfaces.IBoardValidator;

public class StandardBoardValidator implements IBoardValidator {
    @Override
    public void validate(int rows, int columns, int totalMines) {
        if (rows <= 0 || columns <= 0) {
            throw new IllegalArgumentException("Board dimensions must be positive");
        }

        int maxPossibleMines = rows * columns;
        if (totalMines < 0 || totalMines > maxPossibleMines) {
            throw new IllegalArgumentException("Invalid number of mines: " + totalMines);
        }
    }
}
