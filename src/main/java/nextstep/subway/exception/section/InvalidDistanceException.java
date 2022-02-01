package nextstep.subway.exception.section;

import nextstep.subway.exception.ServiceException;

public class InvalidDistanceException extends ServiceException {

    private static final String MESSAGE = "기존 구간의 길이보다 커야 합니다. - %s";

    public InvalidDistanceException(int distance) {
        super(String.format(MESSAGE, distance));
    }

}
