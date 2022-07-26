package nextstep.subway.line.ui.errors;

import nextstep.subway.common.errors.dto.ErrorResponse;
import nextstep.subway.common.errors.errorcode.ErrorCode;
import nextstep.subway.common.errors.utils.ExceptionHandlerUtils;
import nextstep.subway.line.domain.exception.*;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class LineExceptionHandler {

    @ExceptionHandler(IllegalSectionOperationException.class)
    public ResponseEntity<ErrorResponse> illegalSectionOperationException(IllegalSectionOperationException e) {
        ErrorCode errorCode = LineErrorCode.ILLEGAL_SECTION_OPERATION;
        ErrorResponse errorResponse = ExceptionHandlerUtils.toErrorResponse(errorCode, e);
        return ExceptionHandlerUtils.toResponseEntity(errorCode, errorResponse);
    }
}
