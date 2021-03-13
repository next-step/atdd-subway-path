package nextstep.subway.exceptions;

public class InvalidSectionDistanceException extends RuntimeException {
    public static final String DEFAULT_MSG = "기존 구간의 길이보다 큰 길이의 구간은 추가할 수 없습니다.";

    public InvalidSectionDistanceException() {
        super(DEFAULT_MSG);
    }

    public InvalidSectionDistanceException(String message) {
        super(message);
    }
}
