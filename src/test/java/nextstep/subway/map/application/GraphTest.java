package nextstep.subway.map.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineStation;
import nextstep.subway.line.domain.LineStations;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LineStationResponse;
import nextstep.subway.map.dto.PathResult;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class GraphTest {

    private List<Line> lines;

    @BeforeEach
    void setUp() {
        Line line1 = new Line("2호선", "GREEN", LocalTime.of(5, 30), LocalTime.of(23, 30), 10);
        Line line2 = new Line("3호선", "ORANGE", LocalTime.of(5, 30), LocalTime.of(23, 30), 10);
        Line line3 = new Line("4호선", "BLUE", LocalTime.of(5, 30), LocalTime.of(23, 30), 10);

        line1.addLineStation(new LineStation(1L, null, 0, 0));
        line1.addLineStation(new LineStation(2L, 1L, 5, 10));
        line1.addLineStation(new LineStation(3L, 2L, 5, 10));

        line2.addLineStation(new LineStation(2L, null, 0, 0));
        line2.addLineStation(new LineStation(4L, 2L, 5, 10));

        line3.addLineStation(new LineStation(1L, null, 0, 0));
        line3.addLineStation(new LineStation(4L, 1L, 1, 10));
        line3.addLineStation(new LineStation(3L, 4L, 3, 10));

        Station station1 = new Station("을지로4가");
        Station station2 = new Station("을지로3가");
        Station station3 = new Station("충무로역");
        Station station4 = new Station("동대문역");

        ReflectionTestUtils.setField(station1, "id", 1L);
        ReflectionTestUtils.setField(station2, "id", 2L);
        ReflectionTestUtils.setField(station3, "id", 3L);
        ReflectionTestUtils.setField(station4, "id", 4L);

        lines = Arrays.asList(line1, line2, line3);
    }

    @Test
    void findShortPath() {
        Graph graph = new Graph();

        PathResult pathResult = graph.findPath(lines, 1L, 3L);

        assertThat(pathResult.getWeight()).isEqualTo(4);
    }
}
