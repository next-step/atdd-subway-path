package nextstep.subway.utils;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.concurrent.atomic.AtomicLong;

public class TestObjectFactory {
    AtomicLong idCounter = new AtomicLong();

    public Station 역생성(String name) {
        Station station = new Station(name);
        ReflectionTestUtils.setField(station, "id", idCounter.getAndAdd(1));
        return station;
    }

    public Line 노선생성(String name) {
        Line line = new Line(name, "red");
        ReflectionTestUtils.setField(line, "id", idCounter.getAndAdd(1));
        return line;
    }
}
