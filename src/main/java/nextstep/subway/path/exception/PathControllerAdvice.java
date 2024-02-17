package nextstep.subway.path.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class PathControllerAdvice {

    private final Logger log = LoggerFactory.getLogger(PathControllerAdvice.class);

    @ExceptionHandler(PathException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String pathExceptionAdvice(PathException e) {
        log.debug("PathException 발생 ::: {}", e.getMessage());
        return e.getMessage();
    }
}
