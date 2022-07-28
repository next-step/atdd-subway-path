package nextstep.subway.domain.exception;

public class StationNotFoundException extends DomainException {
    public StationNotFoundException() {
        super("노선에 존재하지 않는 역입니다");
    }
}
