package nextstep.subway.ui;

import nextstep.subway.exception.NewSectionException;
import nextstep.subway.exception.NotFoundException;
import nextstep.subway.exception.PathException;
import nextstep.subway.exception.RemoveSectionException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ControllerExceptionHandler {
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ExceptionResponse> handleIllegalArgsException(DataIntegrityViolationException e) {
        return getResponseEntity(e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ExceptionResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        return getResponseEntity(e.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleNotFoundException(NotFoundException e) {
        return getResponseEntity(e.getMessage());
    }

    @ExceptionHandler(NewSectionException.class)
    public ResponseEntity<ExceptionResponse> handleNewSectionException(NewSectionException e) {
        return getResponseEntity(e.getMessage());
    }

    @ExceptionHandler(RemoveSectionException.class)
    public ResponseEntity<ExceptionResponse> handleRemoveSectionException(RemoveSectionException e) {
        return getResponseEntity(e.getMessage());
    }

    @ExceptionHandler(PathException.class)
    public ResponseEntity<ExceptionResponse> handleRemoveSectionException(PathException e) {
        return getResponseEntity(e.getMessage());
    }

    private ResponseEntity<ExceptionResponse> getResponseEntity(String e) {
        return new ResponseEntity<>(new ExceptionResponse(e), HttpStatus.BAD_REQUEST);
    }

}
