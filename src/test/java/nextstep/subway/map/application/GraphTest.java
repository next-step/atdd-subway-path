package nextstep.subway.map.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LineStationResponse;
import nextstep.subway.map.dto.PathResult;
import nextstep.subway.map.dto.SearchType;
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
    private List<LineResponse> lines;

    @BeforeEach
    void setUp() {
        Line line1 = new Line("2호선", "GREEN", LocalTime.of(5, 30), LocalTime.of(23, 30), 10);
        Line line2 = new Line("3호선", "ORANGE", LocalTime.of(5, 30), LocalTime.of(23, 30), 10);
        Line line3 = new Line("4호선", "BLUE", LocalTime.of(5, 30), LocalTime.of(23, 30), 10);

        Station station1 = new Station("을지로4가");
        ReflectionTestUtils.setField(station1, "id", 1L);

        Station station2 = new Station("을지로3가");
        ReflectionTestUtils.setField(station2, "id", 2L);

        Station station3 = new Station("충무로역");
        ReflectionTestUtils.setField(station3, "id", 3L);

        Station station4 = new Station("동대문역");
        ReflectionTestUtils.setField(station4, "id", 4L);

        StationResponse stationResponse1 = StationResponse.of(station1);
        StationResponse stationResponse2 = StationResponse.of(station2);
        StationResponse stationResponse3 = StationResponse.of(station3);
        StationResponse stationResponse4 = StationResponse.of(station4);

        LineStationResponse lineStationResponse1 = new LineStationResponse(stationResponse1, null, 2, 2);
        LineStationResponse lineStationResponse2 = new LineStationResponse(stationResponse2, 1L, 2, 2);

        LineStationResponse lineStationResponse3 = new LineStationResponse(stationResponse2, null, 2, 2);
        LineStationResponse lineStationResponse4 = new LineStationResponse(stationResponse3, 2L, 2, 1);

        LineStationResponse lineStationResponse5 = new LineStationResponse(stationResponse1, null, 2, 2);
        LineStationResponse lineStationResponse6 = new LineStationResponse(stationResponse4, 1L, 1, 2);
        LineStationResponse lineStationResponse7 = new LineStationResponse(stationResponse3, 4L, 2, 2);

        LineResponse lineResponse1 = LineResponse.of(line1, Arrays.asList(lineStationResponse1, lineStationResponse2));
        LineResponse lineResponse2 = LineResponse.of(line2, Arrays.asList(lineStationResponse3, lineStationResponse4));
        LineResponse lineResponse3 = LineResponse.of(line3, Arrays.asList(lineStationResponse5, lineStationResponse6, lineStationResponse7));

        lines = Arrays.asList(lineResponse1, lineResponse2, lineResponse3);
    }

    @Test
    void findShortDistancePath() {
        Graph graph = new Graph();

        PathResult pathResult = graph.findPath(lines, 1L, 3L, SearchType.DISTANCE);

        assertThat(pathResult.getStationIds()).containsExactlyElementsOf(Lists.newArrayList(1L, 4L, 3L));
    }

    @Test
    void findShortDurationPath() {
        Graph graph = new Graph();

        PathResult pathResult = graph.findPath(lines, 1L, 3L, SearchType.DURATION);

        assertThat(pathResult.getStationIds()).containsExactlyElementsOf(Lists.newArrayList(1L, 2L, 3L));
    }
}
