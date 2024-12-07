package examen.repositories;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;
import examen.models.*;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Administra la guardado y carga del estado del juego desde/para un archivo CSV.
 */
public class GameStateManager {
    private static final String GAME_STATE_FILEPATH = "src/main/resources/files/minesweeper_state.csv";

    /**
     * Guarda el estado del juego en un archivo CSV.
     *
     * @param game Estado actual del juego.
     */
    public static void saveGameState(Game game) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(GAME_STATE_FILEPATH))) {
            Board board = game.getBoard();
            writer.writeNext(new String[] { "PlayerName", game.getPlayer().getName() });
            writer.writeNext(new String[] { "Rows", String.valueOf(board.getRows()) });
            writer.writeNext(new String[] { "Columns", String.valueOf(board.getColumns()) });
            writer.writeNext(new String[] { "TotalMines", String.valueOf(board.getTotalMines()) });
            writer.writeNext(new String[] { "FlagCount", String.valueOf(board.getFlagCount()) });

            // Guardar ubicaciones de minas
            List<String[]> mineLocations = new ArrayList<>();
            mineLocations.add(new String[] { "MineLocation" });
            Box[][] boxes = board.getBoxes();
            for (int row = 0; row < boxes.length; row++) {
                for (int col = 0; col < boxes[row].length; col++) {
                    if (boxes[row][col].isMine()) {
                        mineLocations.add(new String[] {
                                String.valueOf((char) ('A' + row)),
                                String.valueOf(col + 1)
                        });
                    }
                }
            }
            writer.writeAll(mineLocations);

            // Guardar estado del tablero
            List<String[]> boardState = new ArrayList<>();
            String[] headers = new String[boxes[0].length + 1];
            headers[0] = "Row";
            for (int i = 0; i < boxes[0].length; i++) {
                headers[i + 1] = String.valueOf(i + 1);
            }
            boardState.add(headers);

            for (int row = 0; row < boxes.length; row++) {
                String[] rowData = new String[boxes[row].length + 1];
                rowData[0] = String.valueOf((char) ('A' + row));

                for (int col = 0; col < boxes[row].length; col++) {
                    Box box = boxes[row][col];
                    if (box.isRevealed()) {
                        rowData[col + 1] = box instanceof MinedBox ? "X"
                                : String.valueOf(((EmptyBox) box).getAdjacentMinesCount());
                    } else if (box.isFlagged()) {
                        rowData[col + 1] = "F";
                    } else {
                        rowData[col + 1] = "?";
                    }
                }
                boardState.add(rowData);
            }

            writer.writeAll(boardState);
        } catch (IOException e) {
            System.err.println("**** Error al guardar el estado del juego: ****" + e.getMessage());
        }
    }

    /**
     * Carga el estado del juego desde un archivo CSV.
     *
     * @return Resultado de carga del juego, incluyendo el juego y el conteo de banderas, o null si no se encontró estado guardado.
     */
    public static GameLoadResult loadGameState() {
        try (CSVReader reader = new CSVReader(new FileReader(GAME_STATE_FILEPATH))) {
            List<String[]> savedState = reader.readAll();

            // Extraer metadatos
            String playerName = savedState.get(0)[1];
            int rows = Integer.parseInt(savedState.get(1)[1]);
            int columns = Integer.parseInt(savedState.get(2)[1]);
            int totalMines = Integer.parseInt(savedState.get(3)[1]);
            int flagCount = Integer.parseInt(savedState.get(4)[1]);

            // Crear y configurar el tablero
            Board board = Board.builder()
                    .rows(rows)
                    .columns(columns)
                    .totalMines(totalMines)
                    .build();
            board.initializeEmptyBoard();

            // Restaurar ubicaciones de minas
            int minesStartIndex = 6; // Después de los metadatos y el encabezado "MineLocation"
            int mineCount = 0;
            while (mineCount < totalMines && minesStartIndex + mineCount < savedState.size()) {
                String[] mineLocation = savedState.get(minesStartIndex + mineCount);
                if (mineLocation.length == 2) {
                    int row = mineLocation[0].charAt(0) - 'A';
                    int col = Integer.parseInt(mineLocation[1]) - 1;

                    MinedBox minedBox = new MinedBox();
                    minedBox.setMine(true);
                    minedBox.setXPosition(row);
                    minedBox.setYPosition(col);
                    board.getBoxes()[row][col] = minedBox;
                    mineCount++;
                }
            }

            // Restaurar estado del tablero
            int boardStateStartIndex = minesStartIndex + mineCount + 1;
            for (int i = boardStateStartIndex; i < savedState.size(); i++) {
                String[] rowData = savedState.get(i);
                int boardRow = rowData[0].charAt(0) - 'A';

                for (int col = 1; col < rowData.length; col++) {
                    int boardCol = col - 1;
                    Box box = board.getBoxes()[boardRow][boardCol];
                    String cellData = rowData[col];

                    switch (cellData) {
                        case "X" -> {
                            box = new MinedBox();
                            box.setMine(true);
                            box.reveal();
                            box.setXPosition(boardRow);
                            box.setYPosition(boardCol);
                            board.getBoxes()[boardRow][boardCol] = box;
                        }
                        case "F" -> {
                            box.setFlagged(true);
                            board.increaseFlagCount();
                        }
                        case "?" -> {
                            /* No revelado */ }
                        default -> {
                            try {
                                int adjacentMines = Integer.parseInt(cellData);
                                EmptyBox emptyBox = new EmptyBox();
                                emptyBox.setAdjacentMines(adjacentMines);
                                emptyBox.reveal();
                                emptyBox.setXPosition(boardRow);
                                emptyBox.setYPosition(boardCol);
                                board.getBoxes()[boardRow][boardCol] = emptyBox;
                            } catch (NumberFormatException e) {
                                System.err.println("**** Error al parsear minas adyacentes: ****" + cellData);
                            }
                        }
                    }
                }
            }

            // Asegurar que las minas adyacentes sean calculadas
            board.getAdjacentMineCalculator().calculateAdjacentMines(board.getBoxes());

            Player player = Player.builder().name(playerName).build();
            Game game = Game.builder().board(board).player(player).build();
            return new GameLoadResult(game, flagCount);
        } catch (IOException | CsvException e) {
            return null;
        }
    }

    /**
     * Elimina el estado del juego guardado.
     */
    public static void clearGameState() {
        try {
            java.nio.file.Files.deleteIfExists(java.nio.file.Paths.get(GAME_STATE_FILEPATH));
        } catch (IOException e) {
            System.err.println("**** Error al eliminar el estado del juego: ****" + e.getMessage());
        }
    }

    /**
     * Resultado de la carga del juego.
     */
    public static class GameLoadResult {
        private final Game game;
        private final int flagCount;

        public GameLoadResult(Game game, int flagCount) {
            this.game = game;
            this.flagCount = flagCount;
        }

        public Game getGame() {
            return game;
        }

        public int getFlagCount() {
            return flagCount;
        }
    }
}
