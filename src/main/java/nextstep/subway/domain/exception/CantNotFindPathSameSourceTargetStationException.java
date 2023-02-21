package nextstep.subway.domain.exception;

public class CantNotFindPathSameSourceTargetStationException extends RuntimeException {

    public CantNotFindPathSameSourceTargetStationException() {
        super("출발역과 도착역이 동일하면 안됩니다.");
    }

}
