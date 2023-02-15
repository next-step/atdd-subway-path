package nextstep.subway.ui;

import nextstep.subway.common.ErrorResponse;
import nextstep.subway.common.exception.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ControllerExceptionHandler {
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Void> handleIllegalArgsException(DataIntegrityViolationException e) {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(AlreadyExistException.class)
    protected ResponseEntity<ErrorResponse> handleAlreadyExistException(AlreadyExistException e) {
        final ErrorResponse errorResponse = new ErrorResponse(e.getMessage());

        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(DistanceGreaterThanException.class)
    protected ResponseEntity<ErrorResponse> handleDistanceGreaterThanException(DistanceGreaterThanException e) {
        final ErrorResponse errorResponse = new ErrorResponse(e.getMessage());

        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(NoDeleteOneSectionException.class)
    protected ResponseEntity<ErrorResponse> handleNoDeleteOneSectionException(NoDeleteOneSectionException e) {
        final ErrorResponse errorResponse = new ErrorResponse(e.getMessage());

        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(NoExistLineException.class)
    protected ResponseEntity<ErrorResponse> handleNoExistLineException(NoExistLineException e) {
        final ErrorResponse errorResponse = new ErrorResponse(e.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(NoExistStationException.class)
    protected ResponseEntity<ErrorResponse> handleNoExistStationException(NoExistStationException e) {
        final ErrorResponse errorResponse = new ErrorResponse(e.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(NoLastSectionException.class)
    protected ResponseEntity<ErrorResponse> handleNoLastSectionException(NoLastSectionException e) {
        final ErrorResponse errorResponse = new ErrorResponse(e.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(NoRegisterStationException.class)
    protected ResponseEntity<ErrorResponse> handleNoRegisterStationException(NoRegisterStationException e) {
        final ErrorResponse errorResponse = new ErrorResponse(e.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(NoPathConnectedException.class)
    protected ResponseEntity<ErrorResponse> handleNoPathConnectedException(NoPathConnectedException e) {
        final ErrorResponse errorResponse = new ErrorResponse(e.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
}
