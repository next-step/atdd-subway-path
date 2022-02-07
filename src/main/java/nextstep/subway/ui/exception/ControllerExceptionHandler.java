package nextstep.subway.ui.exception;

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

    @ExceptionHandler({SectionException.class, PathException.class, StationException.class})
    public ResponseEntity<String> handleIllegalArgsException(RuntimeException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
