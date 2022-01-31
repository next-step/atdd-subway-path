package nextstep.subway.exception.section;

public class InvalidDistanceException extends RuntimeException{

    private static final String MESSAGE = "기존 구간의 길이보다 커야 합니다. - %s";

    public InvalidDistanceException(int distance) {
        super(String.format(MESSAGE, distance));
    }

}
