package nextstep.subway.exception.sections;

import nextstep.subway.exception.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class SectionsExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleIllegalArgsException(SectionsException e) {
        ErrorResponse response = ErrorResponse.of(e);
        return new ResponseEntity<>(response, e.getErrorCode().getStatus());
    }
}
