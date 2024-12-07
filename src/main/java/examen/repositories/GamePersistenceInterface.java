package examen.repositories;

import examen.models.Game;

/**
 * Interface para gestionar el estado de los juegos persistentes.
 */
public interface GamePersistenceInterface {
    /**
     * Resultado de la carga de un estado de juego.
     */
    interface IGameLoadResult {
        /**
         * Obtiene la instancia del juego cargada.
         * 
         * @return El objeto Game cargado.
         */
        Game getGame();
    }

    /**
     * Carga el estado del juego desde una fuente persistente.
     * 
     * @return Un objeto IGameLoadResult que contiene el estado del juego cargado, 
     *         o null si no se pudo cargar el juego.
     */
    IGameLoadResult loadGameState();

    /**
     * Guarda el estado actual del juego en una fuente persistente.
     * 
     * @param game El objeto Game que contiene el estado actual del juego.
     */
    void saveGameState(Game game);

    /**
     * Borra el estado del juego de la fuente persistente.
     */
    void clearGameState();
}
