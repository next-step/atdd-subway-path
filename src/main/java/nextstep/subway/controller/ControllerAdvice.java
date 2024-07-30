package nextstep.subway.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.persistence.EntityNotFoundException;

@RestControllerAdvice
public class ControllerAdvice {
    @ExceptionHandler({EntityNotFoundException.class})
    public ResponseEntity<Void> handle(EntityNotFoundException ex) {
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler({UnsupportedOperationException.class})
    public ResponseEntity<Void> handle(UnsupportedOperationException ex) {
        return ResponseEntity.badRequest().build();
    }
}
