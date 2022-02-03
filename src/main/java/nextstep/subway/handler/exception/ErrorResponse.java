package nextstep.subway.handler.exception;

import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

public class ErrorResponse {
    public static ResponseEntity<ErrorResponse> convert(ErrorCode errorCode) {
        return ResponseEntity.status(errorCode.getStatus()).body(new ErrorResponse(errorCode));
    }

    private final LocalDateTime localDateTime;
    private final int status;
    private final String detail;

    private ErrorResponse(ErrorCode errorCode) {
        localDateTime = LocalDateTime.now();
        this.status = errorCode.getStatus();
        this.detail = errorCode.getDetail();
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public int getStatus() {
        return status;
    }

    public String getDetail() {
        return detail;
    }
}
