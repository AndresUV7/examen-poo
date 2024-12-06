package examen.exceptions;

/**
 * Excepción para manejar errores relacionados con el tablero del juego.
 */
public class BoardException extends RuntimeException {
    public BoardException(String message) {
        super(message);
    }

    public BoardException(String message, Throwable cause) {
        super(message, cause);
    }
}
