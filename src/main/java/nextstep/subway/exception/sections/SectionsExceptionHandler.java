package nextstep.subway.exception.sections;

import nextstep.subway.exception.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class SectionsExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> SectionsDeleteExceptionHandler(SectionsDeleteException e) {
        ErrorResponse response = ErrorResponse.of(e);
        return new ResponseEntity<>(response, e.getStatus());
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> SectionsAddExceptionHandler(SectionsAddException e) {
        ErrorResponse response = ErrorResponse.of(e);
        return new ResponseEntity<>(response, e.getStatus());
    }
}
