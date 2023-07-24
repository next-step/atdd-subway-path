package subway.exception.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import subway.exception.SubwayException;
import subway.exception.dto.ErrorResponse;

@ControllerAdvice
public class SubwayExceptionController {

    @ExceptionHandler(SubwayException.class)
    protected ResponseEntity<ErrorResponse> handleSubwayException(SubwayException e) {
        return ResponseEntity
            .status(e.getStatusCode())
            .body(new ErrorResponse(e.getStatusCode(), e.getMessage()));
    }

}
