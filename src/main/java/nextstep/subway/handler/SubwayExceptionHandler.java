package nextstep.subway.handler;

import nextstep.subway.applicaion.dto.ErrorResponse;
import nextstep.subway.exception.BadRequestPathException;
import nextstep.subway.exception.BadRequestSectionsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class SubwayExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(SubwayExceptionHandler.class);

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BadRequestSectionsException.class)
    public @ResponseBody ErrorResponse badRequestSectionsHandler(BadRequestSectionsException ex) {
        log.warn("error " + ex.getMessage() + "[BAD_REQUEST]");
        return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BadRequestPathException.class)
    public @ResponseBody ErrorResponse badRequestPathHandler(BadRequestPathException ex) {
        log.warn("error " + ex.getMessage() + "[BAD_REQUEST]");
        return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public @ResponseBody ErrorResponse badRequestPathHandler(IllegalArgumentException ex) {
        log.warn("error " + ex.getMessage() + "[BAD_REQUEST]");
        return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
    }
}