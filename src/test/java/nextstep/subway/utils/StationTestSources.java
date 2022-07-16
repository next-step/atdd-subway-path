package nextstep.subway.utils;

import nextstep.subway.domain.Station;

public class StationTestSources {

    public static final long upStationId = 100L;
    public static final long downStationId = 101L;

    public static Station upStation() {
        return new Station(upStationId, "upStation");
    }

    public static Station downStation() {
        return new Station(downStationId, "downStation");
    }

}
