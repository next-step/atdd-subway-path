package nextstep.subway.error.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    // Path
    SOURCE_AND_TARGET_SAME(HttpStatus.BAD_REQUEST, "조회하려는 경로의 출발역과 도착역이 같습니다.");


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
