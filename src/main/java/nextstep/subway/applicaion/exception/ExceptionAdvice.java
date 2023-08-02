package nextstep.subway.applicaion.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class ExceptionAdvice {

    @ExceptionHandler(SubwayException.class)
    public ResponseEntity<Object> subwayExceptionHandler(final SubwayException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> ExceptionHandler(final Exception exception) {
        log.error("ExceptionHandler : ", exception);
        return ResponseEntity.internalServerError().body(exception.getMessage());
    }

}