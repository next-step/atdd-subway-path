package nextstep.subway.exception;

import nextstep.subway.exception.line.LineNotFoundException;
import nextstep.subway.exception.line.LineSectionException;
import nextstep.subway.exception.path.PathBadRequestException;
import nextstep.subway.exception.path.PathNotFoundException;
import nextstep.subway.exception.station.StationNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class ExceptionController {
    private final Logger logger = LoggerFactory.getLogger("subway.exception.ExceptionController");

    @ExceptionHandler(LineSectionException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ResponseEntity<ErrorResponse> exception(LineSectionException e, HttpServletRequest req) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY.value(), e.getMessage());
        logger.warn("UNPROCESSABLE_ENTITY occurred on {} {}. Message={}",
                req.getMethod(), req.getRequestURI(), e.getMessage()
        );
        return ResponseEntity.unprocessableEntity().body(errorResponse);
    }

    @ExceptionHandler(PathNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponse> pathNotFoundException(PathNotFoundException e, HttpServletRequest req) {
        return notFoundException(e, req);
    }

    @ExceptionHandler(StationNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponse> stationNotFoundException(StationNotFoundException e, HttpServletRequest req) {
        return notFoundException(e, req);
    }

    @ExceptionHandler(LineNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponse> lineNotFoundException(LineNotFoundException e, HttpServletRequest req) {
        return notFoundException(e, req);
    }

    @ExceptionHandler(PathBadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> exception(PathBadRequestException e, HttpServletRequest req) {
        return badRequestException(e, req);
    }

    private ResponseEntity<ErrorResponse> notFoundException(RuntimeException e, HttpServletRequest req) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.value(), e.getMessage());
        logger.warn("NOT_FOUND occurred on {} {}. Message={}",
                req.getMethod(), req.getRequestURI(), e.getMessage()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    private ResponseEntity<ErrorResponse> badRequestException(RuntimeException e, HttpServletRequest req) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        logger.warn("BAD_REQUEST occurred on {} {}. Message={}",
                req.getMethod(), req.getRequestURI(), e.getMessage()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}
