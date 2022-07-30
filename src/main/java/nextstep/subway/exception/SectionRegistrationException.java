package nextstep.subway.exception;

public class SectionRegistrationException extends RuntimeException {

    private static final String INVALID_SECTION_UP_STATION_EXCEPTION = "새로운 구간의 역 중 하나는 반드시 노선의 상행역이나 하행역에 포함되어야하고, " +
            "나머지 역은 노선에 포함되는 역일 수 없습니다.";

    public SectionRegistrationException() {
        super(INVALID_SECTION_UP_STATION_EXCEPTION);
    }

}