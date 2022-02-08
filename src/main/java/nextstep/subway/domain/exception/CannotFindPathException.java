package nextstep.subway.domain.exception;

public class CannotFindPathException extends RuntimeException {

    public static final String NOT_EXIST_ARRIVAL_STATION = "도착역이 없는 경우 경로를 조회할 수 없습니다.";
    public static final String NOT_EXIST_START_STATION = "출발역이 없는 경우 경로를 조회할 수 없습니다.";
    public static final String SAME_STATIONS = "출발역과 도착역이 동일하면 조회할 수 없습니다.";

    public CannotFindPathException(String message) {
        super(message);
    }

    public static CannotFindPathException notExistArrivalStation() {
        return new CannotFindPathException(NOT_EXIST_ARRIVAL_STATION);
    }

    public static CannotFindPathException notExistStartStation() {
        return new CannotFindPathException(NOT_EXIST_START_STATION);
    }

    public static CannotFindPathException sameStations() {
        return new CannotFindPathException(SAME_STATIONS);
    }
}
