package nextstep.subway.domain.exception;

public class SectionDeleteException extends IllegalArgumentException {
    private static final String MESSAGE = "마지막 구간만 제거가 가능합니다. stationId=%d";

    public SectionDeleteException(Long stationId) {
        super(String.format(MESSAGE, stationId));
    }
}
