package nextstep.subway.common.errors.handler;

import nextstep.subway.common.errors.dto.ErrorResponse;
import nextstep.subway.common.errors.errorcode.CommonErrorCode;
import nextstep.subway.common.errors.errorcode.ErrorCode;
import nextstep.subway.common.errors.utils.ExceptionHandlerUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> methodArgumentNotValidException(MethodArgumentNotValidException e) {
        ErrorCode errorCode = CommonErrorCode.INVALID_PARAMETER;
        ErrorResponse errorResponse = toErrorResponse(errorCode, e.getBindingResult());
        return ExceptionHandlerUtils.toResponseEntity(errorCode, errorResponse);
    }

    private ErrorResponse toErrorResponse(ErrorCode errorCode, BindingResult bindingResult) {
        return ErrorResponse.builder()
                .code(errorCode.name())
                .message(errorCode.getMessage())
                .errors(toValidationErrors(bindingResult))
                .build();
    }

    private List<ErrorResponse.ValidationError> toValidationErrors(BindingResult bindingResult) {
        return bindingResult.getFieldErrors().stream()
                .map(ErrorResponse.ValidationError::of)
                .collect(Collectors.toList());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> illegalArgumentException(IllegalArgumentException e) {
        ErrorCode errorCode = CommonErrorCode.INVALID_PARAMETER;
        ErrorResponse errorResponse = ExceptionHandlerUtils.toErrorResponse(errorCode, e);
        return ExceptionHandlerUtils.toResponseEntity(errorCode, errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> exception(Exception e) {
        ErrorCode errorCode = CommonErrorCode.INTERNAL_SERVER_ERROR;
        ErrorResponse errorResponse = ExceptionHandlerUtils.toErrorResponse(errorCode, e);
        return ExceptionHandlerUtils.toResponseEntity(errorCode, errorResponse);
    }

}
