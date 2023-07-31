package nextstep.subway.unit.utils;

import nextstep.subway.domain.station.Station;

public class StationMother {

    public static Station makeStation(String name) {
        return new Station(name);
    }

}
