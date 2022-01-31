package nextstep.subway.error;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    NOT_FOUND(HttpStatus.NOT_FOUND, "-1001", "Resource Not Found"),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "-10002", "Bad Request"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "-10003", "Internal Server Error"),
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "-10004", "Validation Error"),
    NOT_FOUND_LINE_ERROR(HttpStatus.BAD_REQUEST, "-10005", "노선을 찾을 수 없습니다."),
    DUPLICATE_ERROR(HttpStatus.BAD_REQUEST, "-10006", "이미 등록된 정보입니다."),
    NOT_FOUND_STATION_ERROR(HttpStatus.BAD_REQUEST, "-10007", "지하철 역을 찾을 수 없습니다."),
    NOT_FOUND_SECTION_ERROR(HttpStatus.BAD_REQUEST, "-10008", "구간을 찾을 수 없습니다."),
    FIRST_SECTION_CREATE_ERROR(HttpStatus.BAD_REQUEST, "-10009", "노선 생성시 등록된 구간이 존재하여, 구간 등록이 불가능합니다."),
    UP_STATION_MUST_SAME_LAST_REGISTERED_DOWN_STATION_ERROR(HttpStatus.BAD_REQUEST, "-10010", "새로운 구간의 상행역은 현재 등록되어있는 하행 종점역이어야 합니다."),
    DOWN_STATION_MUST_NOT_CURRENTLY_REGISTERED_STATION_ERROR(HttpStatus.BAD_REQUEST, "-10011", "새로운 구간의 하행역은 현재 등록되어있는 역일 수 없습니다."),
    SECTION_MINIMUM_SIZE_ERROR(HttpStatus.BAD_REQUEST, "-10012", "구간은 2개이상 등록되어있는 경우 삭제 가능합니다."),
    ONLY_THE_LAST_SECTION_CAN_BE_DELETE_ERROR(HttpStatus.BAD_REQUEST, "-10013", "마지막 구간만 삭제 가능합니다."),
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
