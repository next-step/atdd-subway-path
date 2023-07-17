package subway.line.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SubwayMessage {

    LINE_NOT_FOUND(1000L, "존재하지 않는 노선 입니다."),
    SECTION_ADD_STATION_NOT_FOUND_ANYONE_MESSAGE(2000L, "%s와 %s 중 구간을 연결할 역이 모두 없습니다."),
    SECTION_DELETE_LAST_STATION_VALID(2001L, "마지막 구간만 삭제 할 수 있습니다."),
    SECTION_NOT_FOUND_BY_UP_STATION(2002L,"상행역에 해당하는 구간을 찾을 수 없습니다" ),
    SECTION_NOT_FOUND_BY_DOWN_STATION(2003L, "노선에서 해당 하행역에 해당하는 구간이 없습니다."),
    SECTION_OVER_DISTANCE(2004L, "새로 추가되는 구간의 길이는 기존 구간보다 같거나 더 길 수 없습니다."),
    SECTION_NOT_FOUND_UP_STATION_IN_SECTION(2005L, "상행역으로 지정된 구간을 찾을 수 없습니다."),
    SECTION_NOT_FOUND_DOWN_STATION_IN_SECTION(2006L, "하행역으로 지정된 구간을 찾을 수 없습니다."),
    STATION_DELETE_MINIMAL_VALID(3000L, "역은 %d개 이하일 수 없습니다."),
    STATION_NOT_FOUND(3001L, "존재하지 않는 역 입니다"),
    STATION_IS_ALREADY_EXIST_IN_LINE(3002L, "이미 %s과 %s의 구간이 존재합니다."),
    STATION_NOT_FOUND_IN_SECTION(3003L, "삭제 할 역을 구간에서 찾을 수 없습니다.");

    private final long code;
    private final String message;

    public String getFormatMessage(final long number) {
        return String.format(message, number);
    }
    public String getFormatMessage(final String arg1, final String arg2) {
        return String.format(message, arg1, arg2);
    }
}
