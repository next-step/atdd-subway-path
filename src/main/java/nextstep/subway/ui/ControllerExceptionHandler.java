package nextstep.subway.ui;

import nextstep.subway.exception.AddSectionException;
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

    @ExceptionHandler(AddSectionException.class)
    public ResponseEntity<ErrorResult> addSectionException(AddSectionException e) {
        return ResponseEntity.badRequest().body(new ErrorResult(e.getMessage()));
    }
}
