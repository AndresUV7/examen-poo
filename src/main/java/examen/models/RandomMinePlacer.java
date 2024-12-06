package examen.models;

import examen.models.BoardInterfaces.IMineStrategy;

import java.util.Random;

public class RandomMinePlacer implements IMineStrategy {
    
    @Override
    public void placeMines(Box[][] boxes, int totalMines, Random random) {
        int placedMines = 0;
        int rows = boxes.length;
        int columns = boxes[0].length;

        while (placedMines < totalMines) {
            int randomRow = random.nextInt(rows);
            int randomColumn = random.nextInt(columns);

            if (!(boxes[randomRow][randomColumn] instanceof MinedBox)) {
                MinedBox minedBox = new MinedBox();
                minedBox.setXPosition(randomRow);
                minedBox.setYPosition(randomColumn);
                minedBox.setMine(true);
                boxes[randomRow][randomColumn] = minedBox;
                placedMines++;
            }
        }
    }
}
