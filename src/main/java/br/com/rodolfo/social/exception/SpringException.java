package br.com.rodolfo.social.exception;

import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;

public class SpringException {
    private final String message;
    private final HttpStatus status;
    private final int statusCode;
    private final ZonedDateTime timestamp;
    private final String path;

    public SpringException(String message,
                           HttpStatus status,
                           ZonedDateTime timestamp) {
        this.message = message;
        this.status = status;
        this.timestamp = timestamp;
        this.statusCode = status.value();
        this.path = "";
    }

    public SpringException(String message,
                           HttpStatus status,
                           ZonedDateTime timestamp,
                           String path) {
        this.message = message;
        this.status = status;
        this.timestamp = timestamp;
        this.statusCode = status.value();
        this.path = path;
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

    public int getStatusCode() {
        return statusCode;
    }

    public String getPath() {
        return path;
    }
}
