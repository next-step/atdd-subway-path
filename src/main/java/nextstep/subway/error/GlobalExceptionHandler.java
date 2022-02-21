package nextstep.subway.error;

import nextstep.subway.error.exception.AlreadyRegisterStationException;
import nextstep.subway.error.exception.CannotRegisterSectionException;
import nextstep.subway.error.exception.CannotRemoveSectionException;
import nextstep.subway.error.exception.ErrorCode;
import nextstep.subway.error.exception.SourceAndTargetNotConnectedException;
import nextstep.subway.error.exception.SourceAndTargetSameException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Void> handleIllegalArgsException(DataIntegrityViolationException e) {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(AlreadyRegisterStationException.class)
    public ResponseEntity<Void> handleAlreadyRegisterStationException() {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Void> handleIllegalArgumentException() {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(CannotRegisterSectionException.class)
    public ResponseEntity<Void> handleCannotRegisterSectionException() {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(CannotRemoveSectionException.class)
    public ResponseEntity<Void> handleCannotRemoveSectionException() {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(SourceAndTargetNotConnectedException.class)
    public ResponseEntity<Void> handleSourceAndTargetNotConnectedException() {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(SourceAndTargetSameException.class)
    public ResponseEntity<ErrorResponse> handleSourceAndTargetSameException() {
        ErrorResponse errorResponse = new ErrorResponse(ErrorCode.SOURCE_AND_TARGET_SAME);
        return new ResponseEntity<>(errorResponse, errorResponse.getStatus());
    }
}
