package nextstep.subway.exception;

import nextstep.subway.exception.dto.CommonExceptionResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CommonExceptionAdvice {

    @ExceptionHandler(value = {DataIntegrityViolationException.class, IllegalUpdatingStateException.class})
    public ResponseEntity<CommonExceptionResponse> handleDataIntegrityViolationException(Exception e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new CommonExceptionResponse(e));
    }
}
