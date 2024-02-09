package nextstep.subway.common.fixture;

import nextstep.subway.station.domain.Station;
import nextstep.util.ReflectionUtils;

public class StationFactory {
    private StationFactory() {

    }

    public static Station createStation(final String name) {
        return new Station(name);
    }

    public static Station createStation(final Long id, final String name) {
        final Station station = createStation(name);
        ReflectionUtils.injectIdField(station, id);
        return station;
    }

}
