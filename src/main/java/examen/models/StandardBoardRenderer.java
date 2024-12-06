package examen.models;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import examen.models.GameInterfaces.IBoardRenderer;
import examen.models.GameInterfaces.IBoxDisplayStrategy;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class StandardBoardRenderer implements IBoardRenderer {
    
    private final IBoxDisplayStrategy displayStrategy;

    @Override
    public void printBoard(Board board) {
        printBoardWithStrategy(board, displayStrategy);
    }

    @Override
    public void printDetailedBoard(Board board) {
        printBoardWithStrategy(board, this::getDetailedBoxRepresentation);
    }

    private void printBoardWithStrategy(Board board, IBoxDisplayStrategy strategy) {
        String columnLabels = IntStream.range(1, board.getColumns() + 1)
                .mapToObj(String::valueOf)
                .collect(Collectors.joining(" ", "  ", "\n"));
        System.out.print(columnLabels);

        IntStream.range(0, board.getRows())
                .mapToObj(row -> {
                    String rowRepresentation = Arrays.stream(board.getBoxes()[row])
                            .map(strategy::getRepresentation)
                            .collect(Collectors.joining(" "));
                    return String.format("%c %s\n", 'A' + row, rowRepresentation);
                })
                .forEach(System.out::print);
    }

    private String getDetailedBoxRepresentation(Box box) {
        if (box.isRevealed()) {
            return box instanceof MinedBox ? "X" : 
                String.valueOf(((EmptyBox) box).getAdjacentMinesCount());
        } else if (box.isFlagged()) {
            return "F";
        }
        return "?";
    }
}