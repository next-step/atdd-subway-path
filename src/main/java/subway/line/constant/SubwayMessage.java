package subway.line.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SubwayMessage {

    STATION_DELETE_MINIMAL_VALID_MESSAGE(1000L, "역은 %d개 이하일 수 없습니다."),
    SECTION_DELETE_LAST_STATION_VALID_MESSAGE(1001L, "마지막 구간만 삭제 할 수 있습니다."),
//    DOWN_STATION_NOT_MATCH_WITH_UP_STATION(1002L, "기존 노선의 하행역과 추가 하고자 하는 상행역이 일치하지 않습니다."), // TODO : 제거하기
    SECTION_ADD_STATION_DUPLICATION_VALID_MESSAGE(1003L, "기존 노선에 등록된 역은 추가 하고자 하는 구간의 역이 될 수 없습니다."),
    SECTION_ADD_STATION_NOT_FOUND_ANYONE_MESSAGE(1004L, "구간을 연결할 역이 없습니다."),
    LINE_NOT_FOUND_MESSAGE(1005L, "존재하지 않는 노선 입니다."),
    STATION_NOT_FOUND_MESSAGE(1006L, "존재하지 않는 역 입니다"),
    STATION_NOT_FOUND_UP_STATION_IN_LINE_MESSAGE(1007L, "노선에 등록된 상행역이 구간에 없습니다."),
    STATION_IS_ALREADY_EXIST_IN_LINE_MESSAGE(1008L, "이미 %s과 %s의 구간이 존재합니다.");

    private final long code;
    private final String message;

    public String getFormatMessage(final long number) {
        return String.format(message, number);
    }
    public String getFormatMessage(final String arg1, final String arg2) {
        return String.format(message, arg1, arg2);
    }
}
