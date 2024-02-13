package nextstep.subway.global;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import subway.global.exception.AlreadyRegisteredException;
import subway.global.exception.InsufficientStationException;
import subway.global.exception.SectionMismatchException;
import subway.global.exception.StationNotMatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
            SectionMismatchException.class,
            AlreadyRegisteredException.class,
            InsufficientStationException.class,
            StationNotMatchException.class
    })
    public ResponseEntity<ErrorResponse> handleBadRequestException(RuntimeException e) {
        return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
    }
}
