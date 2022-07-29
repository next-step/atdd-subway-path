package nextstep.subway.path.ui.errors;

import nextstep.subway.common.errors.dto.ErrorResponse;
import nextstep.subway.common.errors.errorcode.ErrorCode;
import nextstep.subway.common.errors.utils.ExceptionHandlerUtils;
import nextstep.subway.path.domain.exception.CannotFindPathException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class PathExceptionHandler {

    @ExceptionHandler(CannotFindPathException.class)
    public ResponseEntity<ErrorResponse> cannotFindPathException(CannotFindPathException e) {
        ErrorCode errorCode = PathErrorCode.CANT_FIND_PATH;
        ErrorResponse errorResponse = ExceptionHandlerUtils.toErrorResponse(errorCode, e);
        return ExceptionHandlerUtils.toResponseEntity(errorCode, errorResponse);
    }
}
