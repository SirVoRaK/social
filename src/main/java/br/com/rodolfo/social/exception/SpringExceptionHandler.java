package br.com.rodolfo.social.exception;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@ControllerAdvice
public class SpringExceptionHandler {
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<SpringException> handle(IllegalArgumentException ex) {
        final HttpStatus badRequest = HttpStatus.BAD_REQUEST;
        SpringException springException = new SpringException(
                ex.getMessage(),
                badRequest,
                ZonedDateTime.now(ZoneId.of("America/Sao_Paulo"))
        );
        return new ResponseEntity<SpringException>(springException, badRequest);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<SpringException> handle(InvalidCredentialsException ex) {
        final HttpStatus unauthorized = HttpStatus.UNAUTHORIZED;
        SpringException springException = new SpringException(
                ex.getMessage(),
                unauthorized,
                ZonedDateTime.now(ZoneId.of("America/Sao_Paulo"))
        );
        return new ResponseEntity<SpringException>(springException, unauthorized);
    }

    @ExceptionHandler({TokenExpiredException.class, JWTDecodeException.class})
    public ResponseEntity<SpringException> handle(Exception ex) {
        final HttpStatus unauthorized = HttpStatus.UNAUTHORIZED;
        SpringException springException = new SpringException(
                ex.getMessage(),
                unauthorized,
                ZonedDateTime.now(ZoneId.of("America/Sao_Paulo"))
        );
        return new ResponseEntity<SpringException>(springException, unauthorized);
    }
}
