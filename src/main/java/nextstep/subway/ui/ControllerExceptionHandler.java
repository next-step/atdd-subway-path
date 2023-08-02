package nextstep.subway.ui;

import nextstep.subway.exception.FindPathException;
import nextstep.subway.exception.SectionAddException;
import nextstep.subway.exception.SectionDeleteException;
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

    @ExceptionHandler(SectionDeleteException.class)
    public ResponseEntity<Void> handleSectionDeleteException(SectionDeleteException e) {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(FindPathException.class)
    public ResponseEntity<Void> handleFindPathException(FindPathException e) {
        return ResponseEntity.badRequest().build();
    }
}
