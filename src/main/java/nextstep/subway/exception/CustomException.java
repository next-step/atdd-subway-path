package nextstep.subway.exception;

public class CustomException extends RuntimeException{
    public static final String NO_SECTION_IN_LINE_MSG = "해당 노선에 구간이 존재하지 않습니다.";

    public static final String LINE_HAS_SECTION_AT_LEAST_ONE = "노선은 적어도 1개 이상의 구간을 가져야합니다.";
    public static final String ONLY_CAN_REMOVE_LAST_STATION_MSG = "노선의 마지막 구간만 제거할 수 있습니다.";
    public static final String ONLY_CAN_CREATE_LAST_STATION_MSG = "노선의 마지막 구간에만 추가할 수 있습니다.";
    public static final String DUPLICATE_STATION_MSG = "노선에 중복된 역이 존재합니다.";
    public static final String NOT_EXIST_STATION_IN_LINE = "노선에 해당 역이 존재하지 않습니다.";
    public static final String ADD_STATION_MUST_INCLUDE_IN_LINE = "추가하려는 구간은 상행/하행역 중 하나는 기존 구간에 포함되어 있어야 합니다.";
    public static final String CAN_NOT_ADD_SECTION_CAUSE_DISTANCE = "추가되는 구간이 기존 구간보다 길이가 깁니다.";

    public static final String INVALID_STATIONS_IN_LINE_MSG = "노선에 등록된 역 정보가 유효하지 않습니다.";
    public static final String PATH_MUST_CONTAIN_STATION = "경로 조회에는 조회하려는 역이 등록되어있어야 합니다.";

    public static final String SAME_STATION_CAN_NOT_SEARCH_PATH = "출발역과 도착역이 같은 경우 경로를 조회 할 수 없습니다.";
    public static final String DOES_NOT_CONNECTED_SOURCE_TO_TARGET = "출발역과 도착역이 이어져있지 않아 경로를 조회 할 수 없습니다.";


    public CustomException(String message) {
        super(message);
    }
}
