package br.com.rodolfo.social.exception;

import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;

public class SpringException {
    private final String message;
    private final HttpStatus status;
    private final ZonedDateTime timestamp;

    public SpringException(String message,
                           HttpStatus status,
                           ZonedDateTime timestamp) {
        this.message = message;
        this.status = status;
        this.timestamp = timestamp;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public ZonedDateTime getTimestamp() {
        return timestamp;
    }

    public String getMessage() {
        return message;
    }
}
