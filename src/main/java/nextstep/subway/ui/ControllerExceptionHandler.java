package nextstep.subway.ui;

import nextstep.subway.domain.exception.DomainException;
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

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<Void> handleDomainException() {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(value = IllegalStateException.class)
    public ResponseEntity<Void> handleIllegalStateException() {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(value = IllegalArgumentException.class)
    public ResponseEntity<Void> handleIllegalArgumentException() {
        return ResponseEntity.badRequest().build();
    }
}
