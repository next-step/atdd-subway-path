package nextstep.subway.ui;

import nextstep.subway.exception.exception.EntityAlreadyExistsException;
import nextstep.subway.exception.exception.EntityCannotRemoveException;
import nextstep.subway.exception.exception.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ControllerExceptionHandler {
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Void> handleIllegalArgsException(DataIntegrityViolationException e) {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(EntityAlreadyExistsException.class)
    public ResponseEntity<Void> handleIllegalArgsException(EntityAlreadyExistsException e) {
        return ResponseEntity.unprocessableEntity().build();
    }

    @ExceptionHandler(EntityCannotRemoveException.class)
    public ResponseEntity<Void> handleIllegalArgsException(EntityCannotRemoveException e) {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Void> handleIllegalArgsException(EntityNotFoundException e) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Void> handleIllegalArgsException(IllegalArgumentException e) {
        return ResponseEntity.badRequest().build();
    }
}
