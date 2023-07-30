package nextstep.subway.applicaion.exception;

import lombok.extern.slf4j.Slf4j;
import nextstep.subway.applicaion.exception.domain.CustomException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ApplicationExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity handleCustomException(CustomException e) {
        log.info("[ControllerExceptionHandler] An CustomException occurred");
        e.printStackTrace();

        return ResponseEntity
                .status(e.getStatus())
                .body(new ExceptionResponse(e.getMessage()));
    }
}