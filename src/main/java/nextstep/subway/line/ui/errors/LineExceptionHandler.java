package nextstep.subway.line.ui.errors;

import nextstep.subway.common.errors.dto.ErrorResponse;
import nextstep.subway.common.errors.errorcode.ErrorCode;
import nextstep.subway.common.errors.utils.ExceptionHandlerUtils;
import nextstep.subway.line.domain.exception.CannotAddSectionException;
import nextstep.subway.line.domain.exception.CannotDeleteSectionException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class LineExceptionHandler {

    @ExceptionHandler(CannotDeleteSectionException.class)
    public ResponseEntity<ErrorResponse> cannotDeleteSectionException(CannotDeleteSectionException e) {
        ErrorCode errorCode = LineErrorCode.CANT_DELETE_SECTION;
        ErrorResponse errorResponse = ExceptionHandlerUtils.toErrorResponse(errorCode, e);
        return ExceptionHandlerUtils.toResponseEntity(errorCode, errorResponse);
    }

    @ExceptionHandler(CannotAddSectionException.class)
    public ResponseEntity<ErrorResponse> cannotAddSectionException(CannotAddSectionException e) {
        ErrorCode errorCode = LineErrorCode.CANT_ADD_SECTION;
        ErrorResponse errorResponse = ExceptionHandlerUtils.toErrorResponse(errorCode, e);
        return ExceptionHandlerUtils.toResponseEntity(errorCode, errorResponse);
    }
}
