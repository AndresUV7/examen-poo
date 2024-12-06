package examen.models;

import java.util.Arrays;

import examen.models.GameInterfaces.IBoardManipulation;
import examen.models.GameInterfaces.IBoardRenderer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Game implements IBoardManipulation {
    private Board board;
    private Player player;
    
    @Builder.Default
    private IBoardRenderer renderer = new StandardBoardRenderer(new DefaultBoxDisplayStrategy());

    @Override
    public void revealAllBoxes() {
        Arrays.stream(board.getBoxes())
                .flatMap(Arrays::stream)
                .forEach(Box::reveal);
    }

    @Override
    public int revealAdjacent(int row, int col) {
        return board.revealAdjacent(row, col);
    }

    public int getFlagCount() {
        return board.getFlagCount();
    }

    public void increaseFlagCount() {
        board.setFlagCount(board.getFlagCount() + 1);
    }
    
    public void decreaseFlagCount() {
        board.setFlagCount(board.getFlagCount() - 1);
    }

    public void printBoard() {
        renderer.printBoard(board);
    }

    public void printDetailedBoard() {
        renderer.printDetailedBoard(board);
    }
}