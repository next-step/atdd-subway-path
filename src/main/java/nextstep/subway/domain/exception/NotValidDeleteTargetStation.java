package nextstep.subway.domain.exception;

public class NotValidDeleteTargetStation extends DomainException {
    public NotValidDeleteTargetStation() {
        super("구간의 마지막 역만 제거할 수 있습니다.");
    }
}
