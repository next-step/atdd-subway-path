package subway.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handleSubwayException(ApiException apiException) {
        return new ResponseEntity<>(ErrorResponse.builder()
            .httpStatus(apiException.getStatus())
            .message(apiException.getMessage())
            .errorCode(apiException.getCode())
            .build(), apiException.getStatus());
    }
}
