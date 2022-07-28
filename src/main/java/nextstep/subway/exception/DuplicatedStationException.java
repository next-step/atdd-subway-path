package nextstep.subway.exception;

import nextstep.subway.domain.Station;

public class DuplicatedStationException extends RuntimeException {

    private static final String DUPLICATED_STATION_EXCEPTION = "등록하려는 역이 이미 라인에 존재합니다. " +
            "(등록하려는 역: %s)";

    public DuplicatedStationException(Station station) {
        super(String.format(
                DUPLICATED_STATION_EXCEPTION,
                station.getName()
        ));
    }

}