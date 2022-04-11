package br.com.rodolfo.social.controller;

import br.com.rodolfo.social.exception.SpringException;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@RestController
@Hidden
public class CustomErrorController implements ErrorController {
    public static final String AMERICA_SAO_PAULO = "America/Sao_Paulo";
    public static final ZoneId ZONE = ZoneId.of(AMERICA_SAO_PAULO);

    @RequestMapping("/error")
    public ResponseEntity<SpringException> handleError(HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        HttpStatus notFound = HttpStatus.NOT_FOUND;
        HttpStatus serverError = HttpStatus.INTERNAL_SERVER_ERROR;
        if (status != null && Integer.parseInt(status.toString()) == notFound.value())
            return ResponseEntity
                    .status(notFound)
                    .body(new SpringException(
                            "Resource not found",
                            notFound,
                            ZonedDateTime.now(ZONE),
                            request.getRequestURI()
                    ));
        return ResponseEntity
                .status(serverError)
                .body(new SpringException(
                        "An error occurred, please try again later",
                        serverError,
                        ZonedDateTime.now(ZONE)
                ));
    }

}