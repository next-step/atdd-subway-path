package nextstep.subway.domain.exception;

public class InvalidMatchEndStationException extends IllegalArgumentException {

    private static final String MESSAGE = "새로운 구간의 상행역은 해당 노선의 하행 종점역과 같아야합니다. 요청한 구간의 샹행역 ID=%d";

    public InvalidMatchEndStationException(Long upStationId) {
        super(String.format(MESSAGE, upStationId));
    }
}
