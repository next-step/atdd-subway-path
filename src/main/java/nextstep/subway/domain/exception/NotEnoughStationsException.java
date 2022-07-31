package nextstep.subway.domain.exception;

import nextstep.subway.domain.PathGraph;

public class NotEnoughStationsException extends IllegalArgumentException {

    private static final String MESSAGE = "최소 %d 이상의 지하철 역이 존재해야 합니다.";

    public NotEnoughStationsException() {
        super(String.format(MESSAGE, PathGraph.MINIMUM_STATION_COUNT));
    }
}
