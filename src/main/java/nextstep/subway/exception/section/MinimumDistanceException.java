package nextstep.subway.exception.section;

public class MinimumDistanceException extends RuntimeException {

    private static final String MESSAGE = "구간의 길이는 1 이상이어야 합니다. - %s";

    public MinimumDistanceException(int distance) {
        super(String.format(MESSAGE, distance));
    }

}
