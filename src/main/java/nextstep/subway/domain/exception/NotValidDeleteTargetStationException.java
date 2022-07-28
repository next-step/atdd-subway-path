package nextstep.subway.domain.exception;

public class NotValidDeleteTargetStationException extends DomainException {
    public NotValidDeleteTargetStationException() {
        super("마지막 남은 구간의 역은 제거할 수 없습니다.");
    }
}
