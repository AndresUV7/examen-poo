package examen.exceptions;

/**
 * Excepción para manejar errores relacionados con la carga y guardado del estado del juego.
 */
public class GameStateException extends RuntimeException {
    public GameStateException(String message) {
        super(message);
    }

    public GameStateException(String message, Throwable cause) {
        super(message, cause);
    }
}

