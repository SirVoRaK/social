package br.com.rodolfo.social.exception;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@ControllerAdvice
public class SpringExceptionHandler {

    public static final String AMERICA_SAO_PAULO = "America/Sao_Paulo";
    public static final ZoneId ZONE = ZoneId.of(AMERICA_SAO_PAULO);

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<SpringException> handle(IllegalArgumentException ex) {
        final HttpStatus badRequest = HttpStatus.BAD_REQUEST;
        SpringException springException = new SpringException(
                ex.getMessage(),
                badRequest,
                ZonedDateTime.now(ZONE)
        );
        return new ResponseEntity<SpringException>(springException, badRequest);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<SpringException> handle(InvalidCredentialsException ex) {
        final HttpStatus unauthorized = HttpStatus.UNAUTHORIZED;
        SpringException springException = new SpringException(
                ex.getMessage(),
                unauthorized,
                ZonedDateTime.now(ZONE)
        );
        return new ResponseEntity<SpringException>(springException, unauthorized);
    }

    @ExceptionHandler({TokenExpiredException.class, JWTDecodeException.class})
    public ResponseEntity<SpringException> handle(Exception ex) {
        final HttpStatus unauthorized = HttpStatus.UNAUTHORIZED;
        SpringException springException = new SpringException(
                ex.getMessage(),
                unauthorized,
                ZonedDateTime.now(ZONE)
        );
        return new ResponseEntity<SpringException>(springException, unauthorized);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<SpringException> handle(HttpMessageNotReadableException ex) {
        final HttpStatus badRequest = HttpStatus.BAD_REQUEST;
        SpringException springException = new SpringException(
                ex.getMessage(),
                badRequest,
                ZonedDateTime.now(ZONE)
        );
        return new ResponseEntity<SpringException>(springException, badRequest);
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<SpringException> handle(MissingRequestHeaderException ex) {
        final HttpStatus badRequest = HttpStatus.BAD_REQUEST;
        SpringException springException = new SpringException(
                ex.getMessage(),
                badRequest,
                ZonedDateTime.now(ZONE)
        );
        return new ResponseEntity<SpringException>(springException, badRequest);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<SpringException> handle(NotFoundException ex) {
        final HttpStatus notFound = HttpStatus.NOT_FOUND;
        SpringException springException = new SpringException(
                ex.getMessage(),
                notFound,
                ZonedDateTime.now(ZONE)
        );
        return new ResponseEntity<SpringException>(springException, notFound);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<SpringException> handle(ForbiddenException ex) {
        final HttpStatus forbidden = HttpStatus.FORBIDDEN;
        SpringException springException = new SpringException(
                ex.getMessage(),
                forbidden,
                ZonedDateTime.now(ZONE)
        );
        return new ResponseEntity<SpringException>(springException, forbidden);
    }
}
