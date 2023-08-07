package nextstep.subway.applicaion.constants;

public class ErrorMessage {
    public static final String LENGTH_ERROR = "등록할 구간의 길이가 기존 역 사이 길이보다 크거나 같습니다.";
    public static final String SECTION_ALREADY_EXISTED = "이미 지정된 구간입니다.";
    public static final String CONCLUED_UP_OR_DOWN = "구간으로 등록하려면 상행역 또는 하행역이 포함되어 있어야 합니다.";
    public static final String SECTION_MORE_THAN_TWO = "구간이 하나인 경우 구간을 삭제할 수 없습니다.";
    public static final String STATION_IS_NOT_SELECTED = "노선에 등록되지 않은 역은 제거할 수 없습니다.";
    public static final String SAME_BETWEEN_STATIONS = "출발역과 도착역이 같습니다.";
    public static final String NOT_FOUND_PATH = "조회되지 않는 경로입니다.";
    public static final String NO_LINK_START_STATION = "요청한 출발역은 연결되지 않은 역입니다.";
    public static final String NO_LINK_TARGET_STATION = "요청한 종점은 연결되지 않은 역입니다.";
}
