package examen.models;

public interface GameInterfaces {
    interface IBoardRenderer {
        void printBoard(Board board);
        void printDetailedBoard(Board board);
    }

    interface IBoxDisplayStrategy {
        String getRepresentation(Box box);
    }

    interface IGameAction {
        void execute(Game game);
    }

    interface IBoardManipulation {
        void revealAllBoxes();
        int revealAdjacent(int row, int col);
    }
}