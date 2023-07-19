package nextstep.subway.handler;

import nextstep.subway.applicaion.dto.ErrorResponse;
import nextstep.subway.exception.BadRequestSectionsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ExceptionHandler.class);

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @org.springframework.web.bind.annotation.ExceptionHandler(BadRequestSectionsException.class)
    public @ResponseBody ErrorResponse badRequestSectionsHandler(BadRequestSectionsException ex) {
        log.warn("error " + ex.getMessage() + "[BAD_REQUEST]");
        return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
    }

}