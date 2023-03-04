package nextstep.subway.exception;

public class SectionContainsAllStationException extends RuntimeException {
    private static final String MESSAGE = "새로 등록된 구간의 모든 역이 이미 포함 돼있습니다.";

    public SectionContainsAllStationException() {
        super(MESSAGE);
    }
}
