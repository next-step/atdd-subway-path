package nextstep.subway.common.exception;

import java.util.NoSuchElementException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class SubwayExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponseDto> customExceptionHandler(CustomException e) {
        return ResponseEntity.badRequest().body(new ErrorResponseDto(e.getCode(), e.getMessage()));
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Void> handleNoSuchElementExceptionException(NoSuchElementException e) {
        return ResponseEntity.notFound().build();
    }


    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Void> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.badRequest().build();
    }
}
