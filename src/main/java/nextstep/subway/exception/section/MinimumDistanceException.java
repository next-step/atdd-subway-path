package nextstep.subway.exception.section;

import nextstep.subway.exception.ServiceException;

public class MinimumDistanceException extends ServiceException {

    private static final String MESSAGE = "구간의 길이는 1 이상이어야 합니다. - %s";

    public MinimumDistanceException(int distance) {
        super(String.format(MESSAGE, distance));
    }

}
