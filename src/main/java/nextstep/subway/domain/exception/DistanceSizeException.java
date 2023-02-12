package nextstep.subway.domain.exception;

public class DistanceSizeException extends IllegalArgumentException {
    private static final String MESSAGE = "거리 생성 최솟값 보다 작습니다.";

    public DistanceSizeException() {
        super(MESSAGE);
    }
}
