package examen.exceptions;

/**
 * Excepción para manejar errores relacionados con las acciones del juego.
 */
public class GameActionException extends RuntimeException {
    public GameActionException(String message) {
        super(message);
    }

    public GameActionException(String message, Throwable cause) {
        super(message, cause);
    }
}

