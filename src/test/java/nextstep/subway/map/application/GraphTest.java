package nextstep.subway.map.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineStation;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LineStationResponse;
import nextstep.subway.map.dto.PathResult;
import nextstep.subway.station.dto.StationResponse;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class GraphTest {

    public static final LocalDateTime CREATED_DATE = LocalDateTime.of(2020, 5, 20, 5, 30);
    public static final LocalDateTime MODIFIED_DATE = LocalDateTime.of(2020, 5, 20, 23, 30);
    private List<LineResponse> lines;

    @BeforeEach
    void setUp() {
        Line line1 = new Line("2호선", "GREEN", LocalTime.of(5, 30), LocalTime.of(23, 30), 5);
        Line line2 = new Line("3호선", "ORANGE", LocalTime.of(5, 30), LocalTime.of(23, 30), 5);
        Line line3 = new Line("4호선", "BLUE", LocalTime.of(5, 30), LocalTime.of(23, 30), 5);

        StationResponse stationResponse1 = new StationResponse(1L, "을지로4가", CREATED_DATE, MODIFIED_DATE);
        StationResponse stationResponse2 = new StationResponse(2L, "을지로3가", CREATED_DATE, MODIFIED_DATE);
        StationResponse stationResponse3 = new StationResponse(3L, "충무로역", CREATED_DATE, MODIFIED_DATE);
        StationResponse stationResponse4 = new StationResponse(4L, "동대문역", CREATED_DATE, MODIFIED_DATE);

        LineStationResponse lineStationResponse1 = new LineStationResponse(stationResponse1, null, 2, 2);
        LineStationResponse lineStationResponse2 = new LineStationResponse(stationResponse2, 1L, 2, 2);
        LineStationResponse lineStationResponse3 = new LineStationResponse(stationResponse3, 2L, 2, 2);

        LineStationResponse lineStationResponse4 = new LineStationResponse(stationResponse2, null, 2, 2);
        LineStationResponse lineStationResponse5 = new LineStationResponse(stationResponse3, 2L, 2, 1);

        LineStationResponse lineStationResponse6 = new LineStationResponse(stationResponse1, null, 2, 2);
        LineStationResponse lineStationResponse7 = new LineStationResponse(stationResponse4, 1L, 1, 2);
        LineStationResponse lineStationResponse8 = new LineStationResponse(stationResponse3, 4L, 2, 2);

        LineResponse lineResponse1 = LineResponse.of(line1, Arrays.asList(lineStationResponse1, lineStationResponse2, lineStationResponse3));
        LineResponse lineResponse2 = LineResponse.of(line2, Arrays.asList(lineStationResponse4, lineStationResponse5));
        LineResponse lineResponse3 = LineResponse.of(line3, Arrays.asList(lineStationResponse6, lineStationResponse7, lineStationResponse8));

        lines = Lists.newArrayList(lineResponse1, lineResponse2, lineResponse3);
    }

    @Test
    void findShortPath() {
        Graph graph = new Graph();

        PathResult pathResult = graph.findPath(lines, 1L, 3L);

        assertThat(pathResult.getWeight()).isEqualTo(3);
    }
}
