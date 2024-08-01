package nextstep.subway.handler;

import nextstep.subway.path.exception.PathException;
import nextstep.subway.station.exception.StationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import nextstep.subway.common.constant.ErrorCode;
import nextstep.subway.common.dto.ErrorResponse;
import nextstep.subway.line.exception.LineNotFoundException;
import nextstep.subway.section.exception.SectionException;
import nextstep.subway.station.exception.StationNotFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(LineNotFoundException.class)
    public ResponseEntity<ErrorResponse> LineNotFoundExceptionHandler(LineNotFoundException e) {
        ErrorResponse errorResponse = new ErrorResponse(ErrorCode.getCollectedErrorResponse(e.getMessage()));
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(StationNotFoundException.class)
    public ResponseEntity<ErrorResponse> StationNotFoundExceptionHandler(StationNotFoundException e) {
        ErrorResponse errorResponse = new ErrorResponse(ErrorCode.getCollectedErrorResponse(e.getMessage()));
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(StationException.class)
    public ResponseEntity<ErrorResponse> StationExceptionHandler(StationException e) {
        ErrorResponse errorResponse = new ErrorResponse(ErrorCode.getCollectedErrorResponse(e.getMessage()));
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SectionException.class)
    public ResponseEntity<ErrorResponse> SectionExceptionHandler(SectionException e) {
        ErrorResponse errorResponse = new ErrorResponse(ErrorCode.getCollectedErrorResponse(e.getMessage()));
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PathException.class)
    public ResponseEntity<ErrorResponse> PathExceptionHandler(PathException e) {
        ErrorResponse errorResponse = new ErrorResponse(ErrorCode.getCollectedErrorResponse(e.getMessage()));
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
