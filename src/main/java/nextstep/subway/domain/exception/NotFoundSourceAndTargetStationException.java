package nextstep.subway.domain.exception;

public class NotFoundSourceAndTargetStationException extends RuntimeException {

    public NotFoundSourceAndTargetStationException() {
        super("경로 조회 시 출발역과 도착역을 찾을 수 없습니다.");
    }

}
