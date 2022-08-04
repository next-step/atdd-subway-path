package nextstep.subway.utils;

import nextstep.subway.domain.Station;

import java.util.concurrent.atomic.AtomicLongArray;

public class StationFixture {
    private static final AtomicLongArray STATION_IDS = new AtomicLongArray(100);

    public static final Station 역생성(String name) {
        return new Station(getId(), name);
    }

    private static Long getId() {
        return STATION_IDS.addAndGet(1, 1);
    }
}
