package nextstep.subway.ui;

import nextstep.subway.exception.SectionAddException;
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

    @ExceptionHandler(SectionAddException.class)
    public ResponseEntity<Void> handleSectionAddException(SectionAddException e) {
        return ResponseEntity.badRequest().build();
    }
}
