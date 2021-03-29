package nextstep.subway.common.exception;

import org.springframework.http.HttpStatus;

public enum ApplicationType {
    STATIONS_ALREADY_REGISTERD(HttpStatus.INTERNAL_SERVER_ERROR, "상/하행역 모두 이미 등록되어있는 역입니다."),
    ONE_STATION_MUST_BE_REGISTERED_AT_LEAST(HttpStatus.INTERNAL_SERVER_ERROR, "상/하행역 중 하나는 노선에 등록되어 있어야 합니다."),
    LINE_MUST_BE_HAVE_ONE_SECTION_AT_LEAST(HttpStatus.INTERNAL_SERVER_ERROR, "구간은 최소하나는 등록되어있어야 합니다."),
    ONLY_DOWN_STATIONS_CAN_BE_DELETED(HttpStatus.INTERNAL_SERVER_ERROR, "하행 종점역만 삭제 가능합니다."),
    NOT_FOUND_STATION(HttpStatus.INTERNAL_SERVER_ERROR, "등록되지 않은 역 입니다."),
    NOT_CONNECTED_STATION(HttpStatus.INTERNAL_SERVER_ERROR, "역이 연결되어있지 않습니다."),
    CANNOT_SAME_WITH_SOURCE_AND_TARGET_ID(HttpStatus.INTERNAL_SERVER_ERROR, "시작역과 도착역은 동일할 수 없습니다."),
    SECTION_DISTANCE_CANNOT_BE_LONGER_THAN_CURRENT_SECTION(HttpStatus.INTERNAL_SERVER_ERROR, "새로 등록하려는 구간의 길이는 현재 구간의 길이보다 길 수 없습니다."),
    NOT_REGISTERED_STATION_ON_LINE(HttpStatus.INTERNAL_SERVER_ERROR, "구간에 등록되어있지 않은 역입니다.");


    private final HttpStatus httpStatus;
    private String message;

    ApplicationType(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public HttpStatus getHttpStatus() {
        return this.httpStatus;
    }

    public String getMessage() { return this.message; }

}
