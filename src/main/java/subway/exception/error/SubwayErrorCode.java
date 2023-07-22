package subway.exception.error;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum SubwayErrorCode {

    STATION_NOT_FOUND(HttpStatus.BAD_REQUEST, "지하철역을 찾을 수 없습니다."),

    LINE_NOT_FOUND(HttpStatus.BAD_REQUEST, "노선을 찾을 수 없습니다."),

    NO_MATCH_UP_STATION(HttpStatus.BAD_REQUEST, "구간의 상행역과 노선의 하행 종점역이 일치하지 않습니다."),
    ALREADY_EXIST_DOWN_STATION(HttpStatus.BAD_REQUEST, "구간의 하행역이 노선에 이미 등록되어있습니다."),
    NON_LAST_STATION_DELETE_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "마지막 구간만 삭제할 수 있습니다."),
    SINGLE_SECTION_DELETE_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "단일 구간 노선의 구간은 삭제할 수 없습니다."),
    CANNOT_CREATE_SECTION(HttpStatus.BAD_REQUEST, "구간을 생성할 수 없습니다.")
    ;

    private HttpStatus statusCode;
    private String message;
}
