package nextstep.subway.unit;

import nextstep.subway.domain.Station;

public class StationUnitSteps {

    public static Station 역_추가(final long id, final String name) {
        return new Station(id, name);
    }
}
