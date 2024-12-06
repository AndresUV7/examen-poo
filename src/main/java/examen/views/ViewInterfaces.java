package examen.views;

public class ViewInterfaces {
    // Interfaz para la gestión de la entrada del jugador
    interface InputView {
        String promptAction(String playerName);

        String promptPosition(String action);

        int promptForRows(String playerName);

        int promptForColumns();

        int promptForMines(String playerName, int rows, int columns);

        String promptPlayerName();
    }

    // Interfaz para la visualización de mensajes
    interface MessageView {
        void showWelcomeMessage(String playerName);

        void showVictoryMessage(String playerName);

        void showGameOverMessage(String playerName);

        void showEndGameMessage(String playerName);

        void showFlagCount(int flagsUsed, int totalMines);

        void showInvalidActionMessage();

        void showInvalidPositionMessage();

        void showAlreadyRevealedMessage();

        void showCannotFlagRevealedMessage();

        void showFlaggedMessage(String playerName);

        void showUnflaggedMessage(String playerName);

        void showNoFlagsLeftMessage();
    }

}
