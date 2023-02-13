package nextstep.subway.exception;

import nextstep.subway.domain.Distance;

public class InvalidSectionDistanceException extends RuntimeException {

    private static final String MESSAGE = "지하철 구간의 길이는 %d 이상이어야 합니다 (현재: %d)";

    public InvalidSectionDistanceException(int distance) {
        super(String.format(MESSAGE, Distance.MIN_VALUE, distance));
    }
}
