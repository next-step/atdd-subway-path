package nextstep.subway.support;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import subway.line.exception.LineNotFoundException;
import subway.section.exception.InvalidSectionCreateException;
import subway.section.exception.InvalidSectionDeleteException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = LineNotFoundException.class)
    public ResponseEntity<Void> handleLineNotFound(Exception e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @ExceptionHandler(value = { InvalidSectionCreateException.class, InvalidSectionDeleteException.class, })
    public ResponseEntity<ErrorResponse> handleInvalidSectionUpstationException(SubwayException se) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                             .body(new ErrorResponse(se));
    }

}
