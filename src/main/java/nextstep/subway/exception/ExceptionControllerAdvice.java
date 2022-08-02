package nextstep.subway.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionControllerAdvice {

    @ExceptionHandler(SectionException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(Exception e) {
        return ResponseEntity.badRequest()
            .body(ErrorResponse.builder().message(e.getMessage()).build());
    }

    @ExceptionHandler(PathException.class)
    public ResponseEntity<ErrorResponse> handlePathException(Exception e) {
        return ResponseEntity.badRequest()
            .body(ErrorResponse.builder().message(e.getMessage()).build());
    }

}
