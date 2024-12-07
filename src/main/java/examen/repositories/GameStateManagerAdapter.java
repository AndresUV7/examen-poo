package examen.repositories;

import examen.models.Game;

public class GameStateManagerAdapter implements GamePersistenceInterface {
    @Override
    public IGameLoadResult loadGameState() {
        // Convert GameStateManager's GameLoadResult to the interface's GameLoadResult
        GameStateManager.GameLoadResult managerResult = GameStateManager.loadGameState();
        
        if (managerResult == null) {
            return null;
        }
        
        return new IGameLoadResult() {
            @Override
            public Game getGame() {
                return managerResult.getGame();
            }
        };
    }

    @Override
    public void saveGameState(Game game) {
        GameStateManager.saveGameState(game);
    }

    @Override
    public void clearGameState() {
        GameStateManager.clearGameState();
    }
}