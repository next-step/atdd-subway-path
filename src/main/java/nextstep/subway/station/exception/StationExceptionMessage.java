package nextstep.subway.station.exception;

public class StationExceptionMessage {

    private StationExceptionMessage() {
    }

    public static final String EXCEPTION_MESSAGE_EXIST_STATION = "존재하는 지하철 역 입니다.";
    public static final String EXCEPTION_MESSAGE_NON_EXIST_STATION = "존재하지 않는 지하철 역입니다.";

    public static final String EXCEPTION_MESSAGE_EXIST_STATION_IN_SECTION = "등록할 구간의 상행역과 하행역이 이미 존재합니다.";
    public static final String EXCEPTION_MESSAGE_NON_EXIST_STATION_IN_SECTION = "등록할 구간의 상행역과 하행역이 존재하지 않습니다.";

    public static final String EXCEPTION_MESSAGE_NOT_DELETABLE_STATION = "하행 종점역만 제거할 수 있습니다.";
}
