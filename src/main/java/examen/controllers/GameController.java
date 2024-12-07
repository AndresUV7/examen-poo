package examen.controllers;

import examen.exceptions.BoardException;
import examen.exceptions.GameActionException;
import examen.models.Board;
import examen.models.Box;
import examen.models.Game;
import examen.models.MinedBox;
import examen.models.Player;
import examen.repositories.GamePersistenceInterface;
import examen.repositories.GamePersistenceInterface.IGameLoadResult;
import examen.views.GameView;

/**
 * Controlador principal para manejar la lógica del juego.
 */
public class GameController {
    private Game game;
    private final GameView view;
    private GamePersistenceInterface gamePersistenceManager;
    private boolean gameOver;

    /**
     * Constructor del controlador del juego.
     *
     * @param game Juego actual.
     * @param view Vista para interactuar con el jugador.
     */
    public GameController(Game game, GameView view) {
        this.game = game;
        this.view = view;
        this.gameOver = false;
    }

    /**
     * Configura el gestor de persistencia del juego.
     *
     * @param gamePersistenceManager Gestor de persistencia.
     */
    public void setGamePersistenceManager(GamePersistenceInterface gamePersistenceManager) {
        this.gamePersistenceManager = gamePersistenceManager;
    }

    /**
     * Carga un juego guardado si existe.
     *
     * @return Verdadero si el juego fue cargado, falso en caso contrario.
     */
    public boolean loadGame() {
        if (gamePersistenceManager == null) {
            throw new IllegalStateException("GamePersistenceManager is not set");
        }

        IGameLoadResult loadedGame = gamePersistenceManager.loadGameState();
        if (loadedGame != null) {
            this.game = loadedGame.getGame();
            view.showWelcomeMessage(game.getPlayer().getName());
            System.out.println("==== Juego anterior cargado. ====");
            return true;
        }
        return false;
    }

    /**
     * Inicializa un nuevo juego si no se carga uno existente.
     */
    public void initializeGame() {
        if (gamePersistenceManager == null) {
            throw new IllegalStateException("GamePersistenceManager is not set");
        }

        if (!loadGame()) {
            view._showWelcomeMessage();
            String playerName = view.promptPlayerName();
            int rows = 0;
            int columns = 0;
            boolean validInput = false;
    
            while (!validInput) {
                try {
                    rows = view.promptForRows(playerName);
                    columns = view.promptForColumns();
                    
                    // Validación para asegurarse que filas y columnas son mayores a 1
                    if (rows <= 2 || columns <= 2) {
                        throw new BoardException("**** EL NÚMERO DE FILAS Y COLUMNAS DEBE SER MAYOR QUE 2 NO PUDE SER IGUAL. ****");
                    }
                    
                    validInput = true;  // Si la validación pasa, se termina el bucle
                } catch (BoardException e) {
                    view.showErrorMessage(e.getMessage());  // Mostrar mensaje de error y repetir el ingreso
                }
            }
    
            // Ingresar minas
            int totalMines = 0;
            boolean validMinesInput = false;
            
            while (!validMinesInput) {
                try {
                    totalMines = view.promptForMines(playerName, rows, columns);
                    
                    // Validación para asegurarse que las minas no superen el total de celdas
                    if (totalMines >= rows * columns) {
                        throw new BoardException("EL NÚMERO DE MINAS NO PUEDE SER MAYOR O IGUAL AL NÚMERO TOTAL DE CELDAS.");
                    }
                    
                    validMinesInput = true;  // Si la validación pasa, se termina el bucle
                } catch (BoardException e) {
                    view.showErrorMessage(e.getMessage());  // Mostrar mensaje de error y repetir el ingreso
                }
            }

            Board board = Board.builder()
                .rows(rows)
                .columns(columns)
                .totalMines(totalMines)
                .build();
            board.generateBoard();

            setGame(Game.builder()
                .board(board)
                .player(Player.builder().name(playerName).build())
                .build());
            saveGame();
        }
    }

    /**
     * Guarda el estado actual del juego.
     */
    private void saveGame() {
        if (gamePersistenceManager == null) {
            throw new IllegalStateException("GamePersistenceManager is not set");
        }
        gamePersistenceManager.saveGameState(game);
    }

    /**
     * Limpia el estado guardado del juego.
     */
    private void clearGame() {
        if (gamePersistenceManager == null) {
            throw new IllegalStateException("GamePersistenceManager is not set");
        }
        gamePersistenceManager.clearGameState();
    }

    /**
     * Inicia la ejecución del ciclo principal del juego.
     */
    public void start() {
        String playerName = game.getPlayer().getName();
        game.printBoard();

        while (!gameOver) {
            view.showFlagCount(game.getFlagCount(), game.getBoard().getTotalMines());
            String action = view.promptAction(playerName);

            try {
                switch (action) {
                    case "V":
                        handleRevealAction();
                        break;
                    case "F":
                        handleFlagAction();
                        break;
                    default:
                        view.showInvalidActionMessage();
                        break;
                }
            } catch (GameActionException | BoardException e) {
                view.showErrorMessage(e.getMessage());
            }
        }

        view.showEndGameMessage(playerName);
    }

    /**
     * Maneja la acción de revelar una celda.
     */
    private void handleRevealAction() {
        String position = view.promptPosition("revelar");
        int[] coords = GameView.parseCoordinates(position);

        if (coords == null) {
            throw new GameActionException("Coordenadas inválidas proporcionadas.");
        }

        int row = coords[0];
        int col = coords[1];

        if (row < 0 || row >= game.getBoard().getRows() || col < 0 || col >= game.getBoard().getColumns()) {
            throw new GameActionException("Movimiento inválido: coordenada fuera de rango.");
        }

        Box box = game.getBoard().getBoxes()[row][col];
        if (box.isRevealed()) {
            throw new GameActionException("La celda ya está revelada.");
        } else if (box instanceof MinedBox) {
            view.showGameOverMessage(game.getPlayer().getName());
            game.revealAllBoxes();
            game.printBoard();
            gameOver = true;
            clearGame();
            throw new GameActionException("¡BOOM! Juego terminado.");
        } else {
            game.revealAdjacent(row, col);
            game.printBoard();
            saveGame();

            if (isGameWon()) {
                view.showVictoryMessage(game.getPlayer().getName());
                game.revealAllBoxes();
                game.printBoard();
                gameOver = true;
                clearGame();
            }
        }
    }

    /**
     * Maneja la acción de marcar o desmarcar una celda con una bandera.
     */
    private void handleFlagAction() {
        String position = view.promptPosition("marcar/desmarcar");
        int[] coords = GameView.parseCoordinates(position);

        if (coords == null) {
            view.showInvalidPositionMessage();
            return;
        }

        int row = coords[0];
        int col = coords[1];

        Box box = game.getBoard().getBoxes()[row][col];
        if (box.isRevealed()) {
            view.showCannotFlagRevealedMessage();
        } else {
            if (box.isFlagged()) {
                box.setFlagged(false);
                game.decreaseFlagCount();
                view.showUnflaggedMessage(game.getPlayer().getName());
            } else {
                if (game.getFlagCount() < game.getBoard().getTotalMines()) {
                    box.setFlagged(true);
                    game.increaseFlagCount();
                    view.showFlaggedMessage(game.getPlayer().getName());
                } else {
                    view.showNoFlagsLeftMessage();
                }
            }
            game.printBoard();
            saveGame();
        }
    }

    /**
     * Verifica si el jugador ha ganado el juego.
     *
     * @return Verdadero si todas las casillas no minadas han sido reveladas.
     */
    private boolean isGameWon() {
        return game.getBoard().allNonMinedBoxesRevealed();
    }

    /**
     * Actualiza el juego actual.
     *
     * @param game Nuevo estado del juego.
     */
    public void setGame(Game game) {
        this.game = game;
    }

    /**
     * Procesa un movimiento del jugador.
     *
     * @param row Fila seleccionada.
     * @param col Columna seleccionada.
     */
    public void processPlayerMove(int row, int col) {
        Box box = game.getBoard().getBoxes()[row][col];
        if (!box.isRevealed()) {
            box.reveal();
            if (box instanceof MinedBox) {
                game.setGameOver(true);
            }
        }
    }
}
