package nextstep.subway.support;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import nextstep.subway.line.exception.LineNotFoundException;


@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = LineNotFoundException.class)
    public ResponseEntity<Void> handleLineNotFound(Exception e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @ExceptionHandler(value = { SubwayException.class, })
    public ResponseEntity<ErrorResponse> handleInvalidSectionUpstationException(SubwayException se) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                             .body(new ErrorResponse(se));
    }

}
