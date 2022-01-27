package nextstep.subway.exception;

import java.util.NoSuchElementException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CommonExceptionHandler {

    @ExceptionHandler(DuplicatedElementException.class)
    public ResponseEntity handleDuplicatedElementExceptionException(DuplicatedElementException e) {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity handleNoSuchElementException(NoSuchElementException e) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.badRequest().build();
    }
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity handleIllegalStateException(IllegalStateException e) {
        return ResponseEntity.badRequest().build();
    }

}
