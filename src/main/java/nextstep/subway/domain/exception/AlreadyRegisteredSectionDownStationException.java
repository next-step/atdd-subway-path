package nextstep.subway.domain.exception;

import nextstep.subway.domain.Station;

public class AlreadyRegisteredSectionDownStationException extends IllegalStateException {
    public AlreadyRegisteredSectionDownStationException(Station upStation) {
        super(String.format("already registered %s", upStation.getName()));
    }
}
