package nextstep.subway.ui;

import nextstep.subway.exception.ErrorResponse;
import nextstep.subway.exception.NotRegisteredStationException;
import nextstep.subway.exception.SingleSectionException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Void> handleIllegalArgsException(final DataIntegrityViolationException e) {
        return ResponseEntity.badRequest().build();
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(SingleSectionException.class)
    ErrorResponse handleSingleSectionException(final SingleSectionException e) {
        return ErrorResponse.from(e);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotRegisteredStationException.class)
    ErrorResponse handleNotRegisteredStationException(final NotRegisteredStationException e) {
        return ErrorResponse.from(e);
    }
}
