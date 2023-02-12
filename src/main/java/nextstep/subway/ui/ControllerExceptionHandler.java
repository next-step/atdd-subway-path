package nextstep.subway.ui;

import nextstep.subway.exception.SubwayException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.persistence.EntityNotFoundException;

@ControllerAdvice
public class ControllerExceptionHandler {
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Void> handleIllegalArgsException(DataIntegrityViolationException e) {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Void> handleIllegalArgsException(EntityNotFoundException e) {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(SubwayException.class)
    public ResponseEntity<Void> handleIllegalArgsException(SubwayException e) {
        return ResponseEntity.badRequest().build();
    }

}
