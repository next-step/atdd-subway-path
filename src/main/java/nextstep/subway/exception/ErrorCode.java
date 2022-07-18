package nextstep.subway.exception;

import org.springframework.http.HttpStatus;


public enum ErrorCode {

    NOT_FOUND_SECTION_EXCEPTION(HttpStatus.NOT_FOUND, "해당 구간을 찾을 수 없습니다"),
    ALREADY_BOTH_STATION_REGISTER_EXCEPTION(HttpStatus.BAD_REQUEST, "상행역과 하행역이 이미 노선에 모두 등록되어 있습니다"),
    SECTION_DISTANCE_EXCEPTION(HttpStatus.BAD_REQUEST, "기존의 구간 길이보다 긴 신규구간을 중간에 추가할 수 없습니다"),
    NOT_SAME_DOWN_STATION_EXCEPTION(HttpStatus.BAD_REQUEST, "마지막 구간의 하행종점역이 삭제할 하행종점역과 일치하지 않습니다");

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
