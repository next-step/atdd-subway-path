package subway.line.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class LineControllerAdvice {

    private final Logger log = LoggerFactory.getLogger(LineControllerAdvice.class);

    @ExceptionHandler(LineException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String lineExceptionAdvice(LineException e) {
        log.debug("LineException 발생 ::: {}", e.getMessage());
        return e.getMessage();
    }
}
