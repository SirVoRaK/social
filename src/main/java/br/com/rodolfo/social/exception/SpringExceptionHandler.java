package br.com.rodolfo.social.exception;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MultipartException;

import javax.servlet.http.HttpServletRequest;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@ControllerAdvice
public class SpringExceptionHandler {

    public static final String AMERICA_SAO_PAULO = "America/Sao_Paulo";
    public static final ZoneId ZONE = ZoneId.of(AMERICA_SAO_PAULO);

    private SpringException generateException(Exception ex, HttpStatus status, HttpServletRequest request) {
        return new SpringException(
                ex.getMessage(),
                status,
                ZonedDateTime.now(ZONE),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<SpringException> handle(IllegalArgumentException ex, HttpServletRequest request) {
        final HttpStatus badRequest = HttpStatus.BAD_REQUEST;
        SpringException springException = this.generateException(ex, badRequest, request);
        return new ResponseEntity<SpringException>(springException, badRequest);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<SpringException> handle(InvalidCredentialsException ex, HttpServletRequest request) {
        final HttpStatus unauthorized = HttpStatus.UNAUTHORIZED;
        SpringException springException = this.generateException(ex, unauthorized, request);
        return new ResponseEntity<SpringException>(springException, unauthorized);
    }

    @ExceptionHandler({TokenExpiredException.class, JWTDecodeException.class})
    public ResponseEntity<SpringException> handle(Exception ex, HttpServletRequest request) {
        final HttpStatus unauthorized = HttpStatus.UNAUTHORIZED;
        SpringException springException = this.generateException(ex, unauthorized, request);
        return new ResponseEntity<SpringException>(springException, unauthorized);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<SpringException> handle(HttpMessageNotReadableException ex, HttpServletRequest request) {
        final HttpStatus badRequest = HttpStatus.BAD_REQUEST;
        SpringException springException = this.generateException(ex, badRequest, request);
        return new ResponseEntity<SpringException>(springException, badRequest);
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<SpringException> handle(MissingRequestHeaderException ex, HttpServletRequest request) {
        final HttpStatus badRequest = HttpStatus.BAD_REQUEST;
        SpringException springException = this.generateException(ex, badRequest, request);
        return new ResponseEntity<SpringException>(springException, badRequest);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<SpringException> handle(NotFoundException ex, HttpServletRequest request) {
        final HttpStatus notFound = HttpStatus.NOT_FOUND;
        SpringException springException = this.generateException(ex, notFound, request);
        return new ResponseEntity<SpringException>(springException, notFound);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<SpringException> handle(ForbiddenException ex, HttpServletRequest request) {
        final HttpStatus forbidden = HttpStatus.FORBIDDEN;
        SpringException springException = this.generateException(ex, forbidden, request);
        return new ResponseEntity<SpringException>(springException, forbidden);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<SpringException> handle(UnauthorizedException ex, HttpServletRequest request) {
        final HttpStatus unauthorized = HttpStatus.UNAUTHORIZED;
        SpringException springException = this.generateException(ex, unauthorized, request);
        return new ResponseEntity<SpringException>(springException, unauthorized);
    }

    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<SpringException> handle(MultipartException ex, HttpServletRequest request) {
        final HttpStatus badRequest = HttpStatus.BAD_REQUEST;
        SpringException springException = this.generateException(ex, badRequest, request);
        return new ResponseEntity<SpringException>(springException, badRequest);
    }
}
