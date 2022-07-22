package nextstep.subway.ui;

import nextstep.subway.exception.AddSectionException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestControllerExceptionHandler {

    @ExceptionHandler(AddSectionException.class)
    public ResponseEntity<ErrorResult> addSectionException(AddSectionException e) {
        return ResponseEntity.badRequest().body(new ErrorResult(e.getMessage()));
    }
}
