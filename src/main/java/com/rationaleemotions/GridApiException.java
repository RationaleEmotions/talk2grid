package com.rationaleemotions;

/**
 * Represents all the problems that arise out of this library.
 */
public class GridApiException extends RuntimeException {
    public GridApiException(Throwable t) {
        super(t);
    }

    public GridApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
