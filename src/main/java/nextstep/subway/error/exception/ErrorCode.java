package nextstep.subway.error.exception;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ErrorCode {

    // Entity
    LINE_NOT_FOUND("노선 정보를 찾을 수 없습니다."),

    // Line

    // Sections
    PREVIOUS_SECTION_NOT_FOUND("이전 구간이 존재하지 않습니다."),
    SECTION_NOT_FOUND_ABOUT_UP_AND_DOWN_STATION("상행역 또는 하행역에 대한 구간이 존재하지 않습니다."),
    SECTION_ALREADY_EXISTS("이미 등록되어있는 구간입니다."),
    INVALID_SECTION_DISTANCE("기존의 구간의 길이보다 새로운 구간의 길이가 길거나 클 수 없습니다."),
    CANNOT_REMOVE_LAST_SECTION("노선의 마지막 구간은 삭제할 수 없습니다."),
    SECTION_LIST_IS_EMPTY("구간 목록이 비어있습니다."),
    STATION_NOT_FOUND_IN_SECTION("구간 목록에 존재하지 않는 역입니다."),

    ;
    private final String message;

    ErrorCode(final String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
