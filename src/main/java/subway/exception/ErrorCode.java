package subway.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    INVALID_SECTION_REGISTRATION(BAD_REQUEST, "새로운 구간의 상행역은 노선에 등록된 마지막 구간의 하행역과 같아야 합니다."),
    ALREADY_REGISTERED_STATION(BAD_REQUEST, "새로운 구간의 하행역은 노선에 등록되어 있는 구간들의 역과 중복될 수 없습니다."),
    DELETE_BY_TERMINATE_STATION(BAD_REQUEST, "구간 삭제 시 하행 종점역을 입력해야 합니다."),
    DELETE_OF_ONLY_ONE_SECTION(BAD_REQUEST, "하나의 구간만 있을 경우 삭제할 수 없습니다."),

    STATION_NOT_FOUND(NOT_FOUND, "지하철역을 찾을 수 없습니다."),
    LINE_NOT_FOUND(NOT_FOUND, "지하철 노선을 찾을 수 없습니다."),
    ;

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public String getCode() {
        return name();
    }
}
