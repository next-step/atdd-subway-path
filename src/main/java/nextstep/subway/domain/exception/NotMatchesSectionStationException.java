package nextstep.subway.domain.exception;

import nextstep.subway.domain.Station;

public class NotMatchesSectionStationException extends IllegalStateException {
    public NotMatchesSectionStationException(Station downStation, Station upStation) {
        super(String.format("%s is not matches %s", downStation.getName(), upStation.getName()));
    }
}
