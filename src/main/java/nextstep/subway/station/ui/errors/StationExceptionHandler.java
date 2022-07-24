package nextstep.subway.station.ui.errors;

import nextstep.subway.common.errors.dto.ErrorResponse;
import nextstep.subway.common.errors.errorcode.ErrorCode;
import nextstep.subway.common.errors.utils.ExceptionHandlerUtils;
import nextstep.subway.station.domain.exception.CannotDeleteStationException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class StationExceptionHandler {

    @ExceptionHandler(CannotDeleteStationException.class)
    public ResponseEntity<ErrorResponse> cannotDeleteStationException(CannotDeleteStationException e) {
        ErrorCode errorCode = StationErrorCode.CANT_DELETE_STATION;
        ErrorResponse errorResponse = ExceptionHandlerUtils.toErrorResponse(errorCode, e);
        return ExceptionHandlerUtils.toResponseEntity(errorCode, errorResponse);
    }
}
