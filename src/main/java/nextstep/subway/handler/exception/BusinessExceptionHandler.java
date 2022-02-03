package nextstep.subway.handler.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class BusinessExceptionHandler {

    @ExceptionHandler(value = StationException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(StationException e) {
        return ErrorResponse.convert(e.getErrorCode());
    }
}
