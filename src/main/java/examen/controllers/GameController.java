package examen.controllers;

import examen.models.Board;
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
            int rows = view.promptForRows(playerName);
            int columns = view.promptForColumns();
            int totalMines = view.promptForMines(playerName, rows, columns);

            Board board = Board.builder().rows(rows).columns(columns).totalMines(totalMines).build();
            board.generateBoard();
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
        }

        view.showEndGameMessage(playerName);
    }

    private void handleRevealAction() {
        String playerName = game.getPlayer().getName();
        String position = view.promptPosition("revelar");
        int[] coords = GameView.parseCoordinates(position);

        if (coords == null) {
            view.showInvalidPositionMessage();
            return;
        }

        int row = coords[0];
        int col = coords[1];

        if (game.getBoard().getBoxes()[row][col].isRevealed()) {
            view.showAlreadyRevealedMessage();
        } else if (game.getBoard().getBoxes()[row][col] instanceof MinedBox) {
            view.showGameOverMessage(playerName);
            game.revealAllBoxes();
            game.printBoard();
            gameOver = true;
            clearGame();
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
}
