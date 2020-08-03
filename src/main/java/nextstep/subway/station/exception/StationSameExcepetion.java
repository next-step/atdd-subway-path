package nextstep.subway.station.exception;

public class StationSameExcepetion extends RuntimeException {

    public StationSameExcepetion() {
        super();
    }

    public StationSameExcepetion(String message) {
        super(message);
    }

    public StationSameExcepetion(String message, Throwable cause) {
        super(message, cause);
    }

    public StationSameExcepetion(Throwable cause) {
        super(cause);
    }
}
