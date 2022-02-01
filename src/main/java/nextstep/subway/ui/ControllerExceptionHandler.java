package nextstep.subway.ui;

import nextstep.subway.domain.exception.CannotAddSectionException;
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

    @ExceptionHandler(CannotAddSectionException.class)
    public ResponseEntity<Void> handleICannotAddSectionException(CannotAddSectionException e) {
        return ResponseEntity.badRequest().build();
    }
}
