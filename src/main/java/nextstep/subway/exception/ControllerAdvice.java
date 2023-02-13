package nextstep.subway.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerAdvice {
    @ExceptionHandler(SubwayRestApiException.class)
    public ResponseEntity<?> handlerException(SubwayRestApiException e) {
        return ResponseEntity.status(e.getErrorResponse().getHttpStatus())
                .body(new ErrorResponse(e.getErrorResponse().getMessage()));
    }
}
