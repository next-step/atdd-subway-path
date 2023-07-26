package nextstep.subway.common;

import lombok.RequiredArgsConstructor;
import nextstep.subway.common.exception.BusinessException;
import nextstep.subway.common.exception.ValidationException;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Locale;

@ControllerAdvice
@RequiredArgsConstructor
public class CommonExceptionHandler {

    private final MessageSource messageSource;

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception e) {
        ErrorResponse errorResponse = new ErrorResponse(messageSource.getMessage(e.getMessage(), null, Locale.KOREA));
        return ResponseEntity
                .internalServerError()
                .body(errorResponse);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        ErrorResponse errorResponse = new ErrorResponse(messageSource.getMessage(e.getMessage(), null, Locale.KOREA));
        return ResponseEntity
                .badRequest()
                .body(errorResponse);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {
        ErrorResponse errorResponse = new ErrorResponse(messageSource.getMessage(e.getMessage(), null, Locale.KOREA));
        return ResponseEntity
                .badRequest()
                .body(errorResponse);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(ValidationException e) {
        ErrorResponse errorResponse = new ErrorResponse(messageSource.getMessage(e.getMessage(), null, Locale.KOREA));
        return ResponseEntity
                .badRequest()
                .body(errorResponse);
    }
}
