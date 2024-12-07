package examen.repositories;

import examen.models.Game;

public interface GamePersistenceInterface {
    interface IGameLoadResult {
        Game getGame();
    }

    IGameLoadResult loadGameState();
    void saveGameState(Game game);
    void clearGameState();
}