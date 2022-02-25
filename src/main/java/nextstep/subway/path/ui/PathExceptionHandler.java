package nextstep.subway.path.ui;

import nextstep.subway.path.exception.PathDomainException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class PathExceptionHandler {
    @ExceptionHandler(PathDomainException.class)
    public ResponseEntity<String> handleBusinessException(PathDomainException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
