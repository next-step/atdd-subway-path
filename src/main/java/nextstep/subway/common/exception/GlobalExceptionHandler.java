package nextstep.subway.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(SubwayException.class)
    @ResponseBody
    public ResponseEntity<ExceptionResponse> handleSubwayException(SubwayException e) {
        SubwayExceptionType exceptionType = e.getSubwayExceptionType();
        return ResponseEntity.status(exceptionType.getCode())
            .body(new ExceptionResponse(exceptionType.getCode(), exceptionType.getMessage()));
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseBody
    public ResponseEntity<ExceptionResponse> handlerRuntimeException(RuntimeException e) {
        return ResponseEntity.internalServerError()
            .body(new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()));
    }
}
