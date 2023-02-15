package nextstep.subway.unit;

import nextstep.subway.domain.Station;
import org.springframework.test.util.ReflectionTestUtils;

import static nextstep.subway.unit.TestFixtureLine.reflectionById;

public class TestFixtureStation {

    public static Station 역_생성(final Long id, final String name) {
        final Station station = new Station(name);
        reflectionById(id, station);
        return station;
    }
}
