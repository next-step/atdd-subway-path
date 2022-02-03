package nextstep.subway.ui;

import nextstep.subway.exception.DuplicatedException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.persistence.EntityNotFoundException;
import java.security.InvalidParameterException;

@ControllerAdvice
public class ControllerExceptionHandler {
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Void> handleIllegalArgsException(DataIntegrityViolationException e) {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler({EntityNotFoundException.class, InvalidParameterException.class, MethodArgumentNotValidException.class})
    public ResponseEntity<Void> getUnprocessableEntityStateEntity() {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY.value()).build();
    }

    @ExceptionHandler(DuplicatedException.class)
    public ResponseEntity<Void> getConflictEntity() {
        return ResponseEntity.status(HttpStatus.CONFLICT.value()).build();
    }
}
