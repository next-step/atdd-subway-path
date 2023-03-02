package nextstep.subway.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    SOME_LINE_UPDATE_FIELD_IS_NULL("노선 업데이트 정보는 null일 수 없습니다", HttpStatus.BAD_REQUEST),

    STATION_NOT_FOUND("해당하는 지하철 역을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    LINE_NOT_FOUND("해당하는 지하철 노선을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

    INVALID_NEW_SECTION_STATION("구간으로 추가할 수 없는 상행역 또는 하행역입니다.", HttpStatus.UNPROCESSABLE_ENTITY),
    INVALID_NEW_SECTION_DISTANCE("구간으로 추가할 수 없는 길이입니다.", HttpStatus.UNPROCESSABLE_ENTITY),
    CANNOT_REMOVE_SECTION_WHEN_SECTION_SIZE_IS_MINIMUM("현재 구간의 크기가 최소 크기일 때, 구간을 삭제할 수 없습니다.", HttpStatus.UNPROCESSABLE_ENTITY),
    REMOVE_STATION_IS_NOT_EXISTS_IN_SECTION("지하철 구간에 존재하지 않는 역은 제거할 수 없습니다.", HttpStatus.UNPROCESSABLE_ENTITY),
    SOURCE_AND_TARGET_STATION_IS_SAME("출발역과 도착역이 같을 수 없습니다.", HttpStatus.UNPROCESSABLE_ENTITY),
    SOURCE_AND_TARGET_STATION_IS_NOT_CONNECTED("출발역과 도착역이 연결되어있지 않습니다.", HttpStatus.UNPROCESSABLE_ENTITY),
    ;

    private final String message;
    private final HttpStatus statusCode;

    ErrorCode(final String message, final HttpStatus statusCode) {
        this.message = message;
        this.statusCode = statusCode;
    }
}
