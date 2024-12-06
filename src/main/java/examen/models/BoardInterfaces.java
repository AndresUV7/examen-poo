package examen.models;

import java.util.Random;

public interface BoardInterfaces {
    
    interface IBoardGenerator {
        void generate(Box[][] boxes, int totalMines);
    }

    interface IBoardValidator {
        void validate(int rows, int columns, int totalMines);
    }

    interface IMineStrategy {
        void placeMines(Box[][] boxes, int totalMines, Random random);
    }

    interface IAdjacentMineCalculator {
        void calculateAdjacentMines(Box[][] boxes);
    }
}