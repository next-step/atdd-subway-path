package nextstep.subway.unit.common;

import nextstep.subway.domain.station.entity.Station;
import org.apache.commons.lang3.RandomStringUtils;
import org.mockito.Mockito;

import static org.mockito.Mockito.when;

public class StationFixture {
    public static class Entity {
        public static long id = 0;
        public static Station 랜덤역생성(){
            final String name = RandomStringUtils.randomAlphanumeric(10);
            Station station = Mockito.spy(new Station(name));
            when(station.getId()).thenReturn(id++);
            return station;

        }
    }
}
