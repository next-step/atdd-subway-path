package nextstep.subway.domain.exceptions;

public class StationNotFoundException extends BusinessException {
    public StationNotFoundException() {
        super("존재하지 않는 지하철역 입니다.");
    }
}
