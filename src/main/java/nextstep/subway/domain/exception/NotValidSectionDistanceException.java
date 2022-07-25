package nextstep.subway.domain.exception;

public class NotValidSectionDistanceException extends DomainException {
    public NotValidSectionDistanceException() {
        super("구간의 거리는 1 보다 작을 수 없습니다");
    }
}
