package nextstep.subway.domain.exception;

public class NotValidDeleteTargetStation extends DomainException {
    public NotValidDeleteTargetStation() {
        super("마지막 남은 구간의 역은 제거할 수 없습니다.");
    }
}
