package nextstep.subway.domain.exception;

public class NotValidSectionStationsException extends DomainException {
    public NotValidSectionStationsException() {
        super("구간의 상/하행역 중 한 역만 구간내 존재해야 합니다.");
    }
}
