package nextstep.subway.exception;

import org.springframework.http.HttpStatus;


public enum ErrorCode {

    ALREADY_BOTH_STATION_REGISTER_EXCEPTION(HttpStatus.BAD_REQUEST, "상행역과 하행역이 이미 노선에 모두 등록되어 있습니다");

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
