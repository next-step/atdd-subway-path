package nextstep.subway.common.errors.utils;

import nextstep.subway.common.errors.dto.ErrorResponse;
import nextstep.subway.common.errors.errorcode.ErrorCode;
import org.springframework.http.ResponseEntity;

public class ExceptionHandlerUtils {

    private ExceptionHandlerUtils() {
    }

    public static ErrorResponse toErrorResponse(ErrorCode errorCode) {
        return ErrorResponse.builder()
                .code(errorCode.name())
                .message(errorCode.getMessage())
                .build();
    }

    public static ErrorResponse toErrorResponse(ErrorCode errorCode, Exception e) {
        return ErrorResponse.builder()
                .code(errorCode.name())
                .message(e.getMessage())
                .build();
    }

    public static ResponseEntity<ErrorResponse> toResponseEntity(ErrorCode errorCode, ErrorResponse errorResponse) {
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(errorResponse);
    }
}
