package nextstep.subway.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandler {
    @org.springframework.web.bind.annotation.ExceptionHandler(IllegalDeleteSectionException.class)
    public ResponseEntity<HttpStatus> checkIllegalDeleteSection() {
        return ResponseEntity.badRequest().build();
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(IllegalDistanceException.class)
    public ResponseEntity<HttpStatus> checkIllegalAddSection() {
        return ResponseEntity.badRequest().build();
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(AllMatchSectionException.class)
    public ResponseEntity<HttpStatus> checkIllegalSectionException() {
        return ResponseEntity.badRequest().build();
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(NoMatchSectionException.class)
    public ResponseEntity<HttpStatus> checkNoMatchException() {
        return ResponseEntity.badRequest().build();
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(NotMatchDeleteSectionException.class)
    public ResponseEntity<HttpStatus> checkNotMatchDeleteSectionException() {
        return ResponseEntity.badRequest().build();
    }
}
