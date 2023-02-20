package nextstep.subway.domain.path;

public class DepartureDestinationCannotReachableException extends RuntimeException {
    public final static String MESSAGE = "경로를 찾을 수 없습니다.";

    public DepartureDestinationCannotReachableException() {
        super(MESSAGE);
    }

}
