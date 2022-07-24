package nextstep.subway.ui;

import nextstep.subway.exception.AddSectionException;
import nextstep.subway.exception.DeleteSectionException;
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

    @ExceptionHandler(DeleteSectionException.class)
    public ResponseEntity<ErrorResult> deleteSectionException(DeleteSectionException e) {
        return ResponseEntity.badRequest().body(new ErrorResult(e.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResult> illegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(new ErrorResult("잘못된 입력 값 입니다."));
    }

}
