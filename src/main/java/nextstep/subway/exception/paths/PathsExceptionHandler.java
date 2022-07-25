package nextstep.subway.exception.paths;

import nextstep.subway.exception.BusinessException;
import nextstep.subway.exception.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class PathsExceptionHandler {

    @ExceptionHandler({NotConnectedPathException.class})
    public ResponseEntity<ErrorResponse> notFoundPathExceptionHandler(BusinessException e) {
        ErrorResponse response = ErrorResponse.of(HttpStatus.NOT_FOUND, e);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> sameStationExceptionHandler(SameStartArrivalStationException e) {
        ErrorResponse response = ErrorResponse.of(HttpStatus.BAD_REQUEST, e);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
