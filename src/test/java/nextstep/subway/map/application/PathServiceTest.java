package nextstep.subway.map.application;

import nextstep.subway.line.application.LineService;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LineStationResponse;
import nextstep.subway.map.dto.PathResponse;
import nextstep.subway.map.dto.PathResult;
import nextstep.subway.map.dto.SearchType;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.station.dto.StationResponse;
import nextstep.subway.station.exception.StationNotFoundException;
import nextstep.subway.station.exception.StationSameExcepetion;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import static java.util.Optional.ofNullable;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class PathServiceTest {
    @Mock
    private LineService lineService;
    @Mock
    private Graph graph;

    @Mock
    private StationRepository stationRepository;

    private PathService pathService;

    private List<LineResponse> lines;
    private PathResult pathResult;
    private Station station1;
    private Station station2;
    private Station station3;
    private Station station4;

    @BeforeEach
    void setUp() {
        pathService = new PathService(lineService, graph, stationRepository);

        station1 = new Station("을지로4가");
        ReflectionTestUtils.setField(station1, "id", 1L);

        station2 = new Station("을지로3가");
        ReflectionTestUtils.setField(station2, "id", 2L);

        station3 = new Station("충무로역");
        ReflectionTestUtils.setField(station3, "id", 3L);

        station4 = new Station("동대문역");
        ReflectionTestUtils.setField(station4, "id", 4L);

        StationResponse stationResponse1 = StationResponse.of(station1);
        StationResponse stationResponse2 = StationResponse.of(station2);
        StationResponse stationResponse3 = StationResponse.of(station3);
        StationResponse stationResponse4 = StationResponse.of(station4);

        Line line1 = new Line("2호선", "GREEN", LocalTime.of(5, 30), LocalTime.of(23, 30), 10);
        ReflectionTestUtils.setField(line1, "id", 1L);
        Line line2 = new Line("3호선", "ORANGE", LocalTime.of(5, 30), LocalTime.of(23, 30), 10);
        ReflectionTestUtils.setField(line2, "id", 2L);
        Line line3 = new Line("4호선", "BLUE", LocalTime.of(5, 30), LocalTime.of(23, 30), 10);
        ReflectionTestUtils.setField(line2, "id", 3L);

        LineStationResponse lineStationResponse1 = new LineStationResponse(stationResponse1, null, 2, 2);
        LineStationResponse lineStationResponse2 = new LineStationResponse(stationResponse2, 1L, 2, 2);

        LineStationResponse lineStationResponse3 = new LineStationResponse(stationResponse2, null, 2, 2);
        LineStationResponse lineStationResponse4 = new LineStationResponse(stationResponse3, 2L, 2, 1);

        LineStationResponse lineStationResponse5 = new LineStationResponse(stationResponse1, null, 2, 2);
        LineStationResponse lineStationResponse6 = new LineStationResponse(stationResponse4, 1L, 1, 2);
        LineStationResponse lineStationResponse7 = new LineStationResponse(stationResponse3, 4L, 2, 2);

        List<LineStationResponse> lineStationResponses1 = Arrays.asList(lineStationResponse1, lineStationResponse2);
        List<LineStationResponse> lineStationResponses2 = Arrays.asList(lineStationResponse3, lineStationResponse4);
        List<LineStationResponse> lineStationResponses3 = Arrays.asList(lineStationResponse5, lineStationResponse6, lineStationResponse7);

        LineResponse lineResponse1 = new LineResponse(line1.getId(), line1.getName(), line1.getColor(), LocalTime.of(5, 30), LocalTime.of(23, 30), 5, lineStationResponses1, LocalDateTime.now(), LocalDateTime.now());
        LineResponse lineResponse2 = new LineResponse(line2.getId(), line2.getName(), line2.getColor(), LocalTime.of(5, 30), LocalTime.of(23, 30), 5, lineStationResponses2, LocalDateTime.now(), LocalDateTime.now());
        LineResponse lineResponse3 = new LineResponse(line3.getId(), line3.getName(), line3.getColor(), LocalTime.of(5, 30), LocalTime.of(23, 30), 5, lineStationResponses3, LocalDateTime.now(), LocalDateTime.now());

        lines = Arrays.asList(lineResponse1, lineResponse2, lineResponse3);

        pathResult = new PathResult(Lists.newArrayList(1L, 4L, 3L), 3);
    }

    @DisplayName("최단거리 조회")
    @Test
    void findPath() {
        when(lineService.findAllLineAndStations()).thenReturn(lines);
        when(stationRepository.findById(anyLong())).thenReturn(ofNullable(station1));
        when(stationRepository.findById(anyLong())).thenReturn(ofNullable(station2));
        when(graph.findPath(anyList(), anyLong(), anyLong())).thenReturn(pathResult);

        PathResponse pathResponse = pathService.findPath(1L, 3L, SearchType.DISTANCE);

        assertThat(pathResponse.getDistance()).isEqualTo(3);
    }

    @DisplayName("존재하지 않은 역 조회")
    @Test
    void findStationNull() {
        assertThatExceptionOfType(StationNotFoundException.class)
                .isThrownBy(() -> pathService.findPath(null, null, SearchType.DISTANCE));
    }

    @DisplayName("동일한 역 조회")
    @Test
    void sameStation() {
        Long source = 1L;
        Long target = 1L;

        assertThatExceptionOfType(StationSameExcepetion.class)
                .isThrownBy(() -> pathService.findPath(source, target, SearchType.DISTANCE));
    }

}
