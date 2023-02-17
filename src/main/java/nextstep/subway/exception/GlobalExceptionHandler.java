package nextstep.subway.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(SubwayRuntimeException.class)
    public ResponseEntity<Void> handleSubwayException(SubwayRuntimeException e) {
        return ResponseEntity.badRequest().build();
    }
}
