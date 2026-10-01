package co.com.srdejo.infrastructure.exceptionhandler;

import lombok.Getter;

@Getter
public enum ExceptionResponse {
    NO_DATA_FOUND("No data found for the requested petition"),
    INVALID_REQUEST("Invalid request body");

    private final String message;

    ExceptionResponse(String message) {
        this.message = message;
    }
}
