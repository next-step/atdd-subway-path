package nextstep.subway.exception;

public class IdenticalSourceTargetNotAllowedException extends RuntimeException {

    public static final String MESSAGE = "출발역과 도착역은 같을 수 없습니다. (출발역: %s, 도착역: %s)";

    public IdenticalSourceTargetNotAllowedException(String source, String target) {
        super(String.format(MESSAGE, source, target));
    }
}
