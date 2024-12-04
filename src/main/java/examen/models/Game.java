package examen.models;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class Game {
    private Board board;
    private Player player;

    public void printBoard() {
        // Generar etiquetas de columnas (números)
        String columnLabels = IntStream.range(1, board.getColumns() + 1)
                .mapToObj(String::valueOf)
                .collect(Collectors.joining(" ", "  ", "\n"));
        System.out.print(columnLabels);

        // Imprimir tablero con etiquetas de filas (letras)
        IntStream.range(0, board.getRows())
                .mapToObj(row -> {
                    // Convertir cada fila a su representación con etiqueta de fila
                    String rowRepresentation = Arrays.stream(board.getBoxes()[row])
                            .map(this::getBoxRepresentation)
                            .collect(Collectors.joining(" "));
                    return String.format("%c %s\n", 'A' + row, rowRepresentation);
                })
                .forEach(System.out::print);
    }

    public void printBoardAux() {
        // Generar etiquetas de columnas (números)
        String columnLabels = IntStream.range(1, board.getColumns() + 1)
                .mapToObj(String::valueOf)
                .collect(Collectors.joining(" ", "  ", "\n"));
        System.out.print(columnLabels);

        // Imprimir tablero con etiquetas de filas (letras)
        IntStream.range(0, board.getRows())
                .mapToObj(row -> {
                    // Convertir cada fila a su representación detallada con etiqueta de fila
                    String rowRepresentation = Arrays.stream(board.getBoxes()[row])
                            .map(this::getDetailedBoxRepresentation)
                            .collect(Collectors.joining(" "));
                    return String.format("%c %s\n", 'A' + row, rowRepresentation);
                })
                .forEach(System.out::print);
    }

    private String getBoxRepresentation(Box box) {
        if (box.isRevealed()) {
            return box instanceof MinedBox ? "X" : String.valueOf(((EmptyBox) box).getAdjacentMinesCount());
        } else if (box.isFlagged()) {
            return "F";
        }
        return "?";
    }

    private String getDetailedBoxRepresentation(Box box) {
        if (box.isRevealed()) {
            if (box instanceof MinedBox) {
                return "X"; // Mina revelada
            } else {
                return String.valueOf(((EmptyBox) box).getAdjacentMinesCount());
            }
        } else if (box.isFlagged()) {
            return "F"; // Casilla marcada
        }
        return "?"; // Casilla no revelada
    }

    public void revealAllBoxes() {
        Arrays.stream(board.getBoxes())
                .flatMap(Arrays::stream)
                .forEach(Box::reveal);
    }

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
}