package nextstep.subway.exception;

import nextstep.subway.domain.Station;

public class SectionRegistrationException extends RuntimeException {

    private static final String INVALID_SECTION_UP_STATION_EXCEPTION = "새로운 구간의 상행역은 해당 노선에 등록되어있는 하행 종점역이어야 합니다. " +
            "(노선 하행 종점역: %s, 새로운 구간 상행역: %s)";

    public SectionRegistrationException(Station station, Station newStation) {
        super(String.format(
                INVALID_SECTION_UP_STATION_EXCEPTION,
                station.getName(),
                station.getName()
        ));
    }

}