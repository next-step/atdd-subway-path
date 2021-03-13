package nextstep.subway.station.exception;

public class StationExceptionMessage {

    private StationExceptionMessage() {
    }

    public static final String EXCEPTION_MESSAGE_EXIST_STATION = "존재하는 지하철 역 입니다.";
    public static final String EXCEPTION_MESSAGE_NON_EXIST_STATION = "존재하지 않는 지하철 역입니다.";

    public static final String EXCEPTION_MESSAGE_EXIST_DOWN_STATION = "하행선이 이미 존재합니다.";
    public static final String EXCEPTION_MESSAGE_NOT_DELETABLE_STATION = "하행 종점역만 제거할 수 있습니다.";

    public static final String EXCEPTION_MESSAGE_NOT_MATCHING_EXISTING_AND_NEW_STATION = "기존 노선의 하행선과 신규 노선의 상행선이 같지 않습니다.";
}
