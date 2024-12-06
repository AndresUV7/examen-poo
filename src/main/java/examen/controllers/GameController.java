package examen.controllers;

import examen.exceptions.BoardException;
import examen.exceptions.GameActionException;
import examen.models.Board;
import examen.models.Box;
import examen.models.Game;
import examen.models.MinedBox;
import examen.models.Player;
import examen.repositories.GameStateManager;
import examen.views.GameView;

public class GameController {
    private Game game;
    private final GameView view;
    private boolean gameOver;

    public GameController(Game game, GameView view) {
        this.game = game;
        this.view = view;
        this.gameOver = false;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public boolean loadGame() {
        GameStateManager.GameLoadResult loadedGame = GameStateManager.loadGameState();
        if (loadedGame != null) {
            this.game = loadedGame.getGame();
            view.showWelcomeMessage(game.getPlayer().getName());

            System.out.println("==== Juego anterior cargado. ====");
            return true;
        }
        return false;
    }

    public void initializeGame() {
        if (!loadGame()) {
            view._showWelcomeMessage();
            String playerName = view.promptPlayerName();
    
            // Ingresar filas y columnas de manera segura
            int rows = 0;
            int columns = 0;
            boolean validInput = false;
    
            while (!validInput) {
                try {
                    rows = view.promptForRows(playerName);
                    columns = view.promptForColumns();
                    
                    // Validación para asegurarse que filas y columnas son mayores a 1
                    if (rows <= 2 || columns <= 2) {
                        throw new BoardException("**** EL NÚMERO DE FILAS Y COLUMNAS DEBE SER MAYOR QUE 1. ****");
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
                        throw new BoardException("El número de minas no puede ser mayor o igual al número total de celdas.");
                    }
                    
                    validMinesInput = true;  // Si la validación pasa, se termina el bucle
                } catch (BoardException e) {
                    view.showErrorMessage(e.getMessage());  // Mostrar mensaje de error y repetir el ingreso
                }
            }
    
            // Si las validaciones pasaron, generamos el tablero
            Board board = Board.builder().rows(rows).columns(columns).totalMines(totalMines).build();
            board.generateBoard(); // Esto genera el tablero
            setGame(Game.builder().board(board).player(Player.builder().name(playerName).build()).build());
            saveGame();
        }
    }
    

    private void saveGame() {
        GameStateManager.saveGameState(game);
    }

    private void clearGame() {
        GameStateManager.clearGameState();
    }

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
            } catch (GameActionException e) {
                view.showErrorMessage(e.getMessage());  // Mostrar el mensaje de error de la excepción
            } catch (BoardException e) {
                view.showErrorMessage(e.getMessage());  // Mostrar el mensaje de error de la excepción
            }
        }

        view.showEndGameMessage(playerName);
    }

    private void handleRevealAction() {
        String playerName = game.getPlayer().getName();
        String position = view.promptPosition("revelar");
        int[] coords = GameView.parseCoordinates(position);

        if (coords == null) {
            throw new GameActionException("Coordenadas inválidas proporcionadas.");
        }

        int row = coords[0];
        int col = coords[1];

        // Verifica si las coordenadas están dentro de los límites del tablero
        if (row < 0 || row >= game.getBoard().getRows() || col < 0 || col >= game.getBoard().getColumns()) {
            throw new GameActionException("**** MOVIMIENTO INVALIDO ****" + System.lineSeparator() + "**** LA COORDENADA INGRESADA NO EXISTE. ****");
        }

        if (game.getBoard().getBoxes()[row][col].isRevealed()) {
            throw new GameActionException("La celda ya ha sido revelada.");
        } else if (game.getBoard().getBoxes()[row][col] instanceof MinedBox) {
            throw new GameActionException("¡BOOM! " + System.lineSeparator() + " ¡JUEGO TERMINADO HAS SALIDO VOLANDO! ");
        } else {
            game.revealAdjacent(row, col);
            game.printBoard();
            saveGame();

            if (isGameWon()) {
                view.showVictoryMessage(playerName);
                game.revealAllBoxes();
                game.printBoard();
                gameOver = true;
                clearGame();
            }
        }
    }

    private void handleFlagAction() {
        String position = view.promptPosition("marcar/desmarcar");
        int[] coords = GameView.parseCoordinates(position);

        if (coords == null) {
            view.showInvalidPositionMessage();
            return;
        }

        int row = coords[0];
        int col = coords[1];

        var box = game.getBoard().getBoxes()[row][col];
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

    private boolean isGameWon() {
        return game.getBoard().allNonMinedBoxesRevealed();
    }

    public void processPlayerMove(int row, int col) {
        if (row < 0 || row >= game.getBoard().getRows() || col < 0 || col >= game.getBoard().getColumns()) {
        }

        Box box = game.getBoard().getBoxes()[row][col];
        if (box.isRevealed()) {
        }

        box.reveal();
        if (box instanceof MinedBox && ((MinedBox) box).isMine()) {
            game.setGameOver(true);
        }
    }
}
