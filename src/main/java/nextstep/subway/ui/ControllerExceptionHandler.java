package nextstep.subway.ui;

import nextstep.subway.domain.exception.CannotAddSectionException;
import nextstep.subway.domain.exception.CannotDeleteSectionException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<String> handleICannotAddSectionException(CannotAddSectionException e) {
        return ResponseEntity.badRequest()
                .body(e.getMessage());
    }

    @ExceptionHandler(CannotDeleteSectionException.class)
    public ResponseEntity<String> handleICannotDeleteSectionException(CannotDeleteSectionException e) {
        return ResponseEntity.badRequest()
                .body(e.getMessage());
    }
}
