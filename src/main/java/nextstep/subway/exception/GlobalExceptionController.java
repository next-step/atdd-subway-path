package nextstep.subway.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionController {

    @ExceptionHandler(InvalidSectionDistanceException.class)
    public ResponseEntity handleInvalidSectionDistanceException(InvalidSectionDistanceException e) {
        return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ExistUpAndDownStationException.class)
    public ResponseEntity handleExistUpAndDownStationException(ExistUpAndDownStationException e) {
        return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CanNotFoundSectionToAddException.class)
    public ResponseEntity handleCanNotFoundSectionToAddException(CanNotFoundSectionToAddException e) {
        return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SameSourceAndTargetException.class)
    public ResponseEntity handleSameSourceAndTargetException(SameSourceAndTargetException e) {
        return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(LineDisconnectedException.class)
    public ResponseEntity handleLineDisconnectedException(LineDisconnectedException e) {
        return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
