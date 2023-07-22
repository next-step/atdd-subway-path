package nextstep.subway.applicaion.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ApplicationExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity handleIllegalArgumentException(IllegalArgumentException e) {
        log.info("[ControllerExceptionHandler] An IllegalArgumentException occurred");
        e.printStackTrace();

        return ResponseEntity
                .badRequest()
                .body(e.getMessage());
    }
}