package nextstep.subway.unit.fixture;

import nextstep.subway.domain.Station;
import org.springframework.test.util.ReflectionTestUtils;

public class StationFixture {

    private static Long id = 1L;

    public static Station 지하철역_생성(String name) {
        Station station = new Station(name);
        ReflectionTestUtils.setField(station,"id",id++);
        return station;
    }
}
