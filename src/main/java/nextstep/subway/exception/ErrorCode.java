package nextstep.subway.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    INVALID_UP_STATION(HttpStatus.BAD_REQUEST, "새로운 구간의 상행역은 해당 노선에 등록되어있는 하행 종점역이어야 한다."),
    INVALID_DOWN_STATION(HttpStatus.BAD_REQUEST, "새로운 구간의 하행역은 해당 노선에 등록되어있는 역일 수 없다."),
    SECTION_IS_ONE(HttpStatus.BAD_REQUEST, "노선에 등록된 구간이 하나일 경우 구간을 삭제할 수 없습니다."),
    NOT_DOWN_STATION(HttpStatus.BAD_REQUEST, "노선에 등록된 하행역만 제거할 수 있습니다."),
    ;

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
