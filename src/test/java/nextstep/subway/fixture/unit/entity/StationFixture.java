package nextstep.subway.fixture.unit.entity;

import static org.mockito.BDDMockito.when;
import static org.mockito.Mockito.spy;

import nextstep.subway.entity.Station;

public class StationFixture {

    public static Station of(long id) {

        Station station = spy(new Station(id + "번"));
        when(station.getId()).thenReturn(id);

        return station;
    }

    public static Station of(String name) {
        return new Station(name);
    }
}
