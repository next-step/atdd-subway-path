package nextstep.subway.exception;

public class InvalidSectionDistanceException extends RuntimeException {
    private static final String MESSAGE = "추가된 구간의 길이가 기존 구간의 길이보다 작아야 합니다.";

    public InvalidSectionDistanceException() {
        super(MESSAGE);
    }
}
