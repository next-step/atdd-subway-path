package nextstep.subway.error;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "-10003", "Internal Server Error"),
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "-10004", "Validation Error"),
    NOT_FOUND_LINE_ERROR(HttpStatus.BAD_REQUEST, "-10005", "노선을 찾을 수 없습니다."),
    DUPLICATE_ERROR(HttpStatus.BAD_REQUEST, "-10006", "이미 등록된 정보입니다."),
    NOT_FOUND_STATION_ERROR(HttpStatus.BAD_REQUEST, "-10007", "지하철 역을 찾을 수 없습니다."),
    NOT_FOUND_SECTION_ERROR(HttpStatus.BAD_REQUEST, "-10008", "구간을 찾을 수 없습니다."),
    FIRST_SECTION_CREATE_ERROR(HttpStatus.BAD_REQUEST, "-10009", "노선 생성시 등록된 구간이 존재하여, 구간 등록이 불가능합니다."),
    SECTION_MINIMUM_SIZE_ERROR(HttpStatus.BAD_REQUEST, "-10012", "구간은 2개이상 등록되어있는 경우 삭제 가능합니다."),
    INVALID_SECTION_DISTANCE_ERROR(HttpStatus.BAD_REQUEST, "-10014", "역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없습니다."),
    ALREADY_EXISTS_STATIONS_ERROR(HttpStatus.BAD_REQUEST, "-10015", "상행역과 하행역이 이미 존재합니다."),
    NOT_EXISTS_ANY_STATIONS_ERROR(HttpStatus.BAD_REQUEST, "-10016", "상행역과 하행역이 모두 존재하지 않습니다."),
    SAME_STATION_ERROR(HttpStatus.BAD_REQUEST, "-10017", "출발역과 도착역은 같을 수 없습니다."),
    NOT_CONNECTED_ERROR(HttpStatus.BAD_REQUEST, "-10018", "연결되어있지 않습니다."),
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
