package nextstep.subway.exception;

import org.springframework.http.HttpStatus;

public enum SubwayError {

    NOT_FOUND_LINE(HttpStatus.BAD_REQUEST, "노선정보를 찾을 수 없습니다."),
    NOT_FOUND_SECTION(HttpStatus.BAD_REQUEST, "구간정보를 찾을 수 없습니다."),
    SECTION_ADD_EXCEPTION(HttpStatus.CONFLICT, "구간정보에 상행역이 현재 노선에 하행 종점역이 아닙니다."),
    SECTION_EXIST_EXCEPTION(HttpStatus.CONFLICT, "구간 상행역, 하행역이 이미 노선에 등록되어 있습니다."),
    SECTION_NOT_EXIST_EXCEPTION(HttpStatus.CONFLICT, "구간 상행역, 하행역이 노선에 하나도 포함되어있지 않습니다."),
    SECTION_DELETE_MIN_SIZE_EXCEPTION(HttpStatus.CONFLICT, "구간이 1개인 경우 삭제할 수 없습니다."),
    SECTION_DELETE_EXCEPTION(HttpStatus.CONFLICT, "구간은 종점역만 삭제가능합니다."),
    SECTION_DISTANCE_OVER_EXCEPTION(HttpStatus.CONFLICT, "구간길이를 초과했습니다."),
    NOT_FOUND_STATION(HttpStatus.BAD_REQUEST, "역정보를 찾을 수 없습니다."),
    NAME_NOT_AVAILABLE(HttpStatus.BAD_REQUEST, "이름정보가 유효하지 않습니다."),
    COLOR_NOT_AVAILABLE(HttpStatus.BAD_REQUEST, "색정보가 유효하지 않습니다."),
    ;

    SubwayError(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    private HttpStatus status;
    private String message;

}
