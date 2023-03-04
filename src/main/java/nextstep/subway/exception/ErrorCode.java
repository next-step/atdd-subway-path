package nextstep.subway.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    INVALID_DISTANCE(HttpStatus.BAD_REQUEST,
        "역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음."),
    INVALID_SECTION(HttpStatus.BAD_REQUEST,
        "상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음."),
    DUPLICATED_SECTION(HttpStatus.BAD_REQUEST, "상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음"),
    ;


    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getMessage() {
        return message;
    }
}
