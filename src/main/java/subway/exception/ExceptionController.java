package subway.exception;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(annotations = Controller.class)
public class ExceptionController {

    @ExceptionHandler(SubwayBadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse exception(SubwayBadRequestException e) {
        return e.getResponse();
    }

    @ExceptionHandler(SubwayNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse exception(SubwayNotFoundException e) {
        return e.getResponse();
    }
}
