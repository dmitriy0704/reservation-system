package dev.folomkin.reservationsystem.web;

import dev.folomkin.reservationsystem.reservations.ReservationService;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    public final Logger log = LoggerFactory.getLogger(ReservationService.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGenericException(Exception e) {

        var errorDto = new ErrorResponseDto(
                "Internal server error",
                e.getMessage(),
                LocalDateTime.now()
        );

        log.error("Handle exception", e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorDto);
    }


    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleEntityNotFound(EntityNotFoundException e) {

        var errorDto = new ErrorResponseDto(
                "Entity not found exception",
                e.getMessage(),
                LocalDateTime.now()
        );
        log.error("Handle entityNotFoundException", e);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorDto);
    }


    @ExceptionHandler(
            exception = {
                    IllegalArgumentException.class,
                    IllegalStateException.class,
                    MethodArgumentNotValidException.class
            }
    )
    public ResponseEntity<ErrorResponseDto> handleBadRequest(Exception e) {
        var errorDto = new ErrorResponseDto(
                "Bad request",
                e.getMessage(),
                LocalDateTime.now()
        );

        log.error("Handle illegalArgumentException", e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDto);
    }
}
