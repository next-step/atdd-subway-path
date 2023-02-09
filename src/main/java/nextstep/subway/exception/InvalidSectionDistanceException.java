package nextstep.subway.exception;

public class InvalidSectionDistanceException extends RuntimeException {

    private static final String MESSAGE = "새로 추가하려는 구간의 길이는 기존 구간의 길이보다 작아야 합니다.\n"
        + "현재 구간의 길이: %d, 추가하려는 구간의 길이: %d";

    public InvalidSectionDistanceException(int existingSectionDistance, int addingSectionDistance) {
        super(String.format(MESSAGE, existingSectionDistance, addingSectionDistance));
    }
}
