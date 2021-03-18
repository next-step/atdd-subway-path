package nextstep.subway.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler({DuplicateStationException.class,NotEqualsLastStationException.class})
    public ResponseEntity handleLineException(Exception error) {
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity handleRuntimeException(RuntimeException error) {
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }

}