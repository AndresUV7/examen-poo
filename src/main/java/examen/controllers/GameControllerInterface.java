package examen.controllers;

public interface GameControllerInterface {

    void initializeGame();

    void start();

    public interface GameActionInterface {
        void handleRevealAction();

        void handleFlagAction();
    }

    interface GameStateInterface {
        boolean loadGame();

        void saveGame();

        void clearGame();

        boolean isGameWon();
    }
}
