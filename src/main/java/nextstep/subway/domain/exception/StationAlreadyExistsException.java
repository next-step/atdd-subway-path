package nextstep.subway.domain.exception;

public class StationAlreadyExistsException extends IllegalArgumentException {

    private static final String MESSAGE = "새로운 구간의 하생역은 해당 노선에 등록되지 않는 역만 가능합니다. 요청한 구간의 하행역 ID=%d";

    public StationAlreadyExistsException(Long downStationId) {
        super(String.format(MESSAGE, downStationId));
    }
}
