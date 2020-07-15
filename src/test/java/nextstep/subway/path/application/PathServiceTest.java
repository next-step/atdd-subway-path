package nextstep.subway.path.application;

import nextstep.subway.exception.NoPathExistsException;
import nextstep.subway.exception.NotFoundException;
import nextstep.subway.exception.NotValidRequestException;
import nextstep.subway.line.application.LineService;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineStation;
import nextstep.subway.line.domain.LineStations;
import nextstep.subway.path.dto.PathResponse;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.dto.StationResponse;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@DisplayName("경로 탐색 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class PathServiceTest {

    @Mock
    private LineService lineService;
    @Mock
    private StationService stationService;
    private PathService pathService;
    private LineStations lineStations1;
    private LineStations lineStations2;
    private LineStation lineStation1;
    private LineStation lineStation2;
    private LineStation lineStation3;
    private LineStation lineStation4;
    private LineStation lineStation5;
    private LineStation lineStation6;

    @BeforeEach
    void setUp() {
        pathService = new PathService(lineService, stationService);

        lineStations1 = mock(LineStations.class);
        lineStations2 = mock(LineStations.class);

        lineStation1 = new LineStation(1L, null, 5, 5);
        lineStation2 = new LineStation(2L, 1L, 5, 5);
        lineStation3 = new LineStation(3L, 2L, 5, 5);
        lineStation4 = new LineStation(3L, null, 5, 5);
        lineStation5 = new LineStation(4L, 3L, 5, 5);
        lineStation6 = new LineStation(4L, 1L, 15, 5);
    }

    @DisplayName("최단 경로 탐색 요청 시, 출발역과 도착역이 같은 경우 에러가 발생한다.")
    @Test
    void findShortestPathWithSameStartAndEndStation() {
        //when
        assertThatThrownBy(() -> pathService.findShortestPath(1L, 1L))
                //then
                .isInstanceOf(NotValidRequestException.class);
    }

    @DisplayName("최단 경로 탐색 요청 시, 출발역과 도착역이 연결이 되어 있지 않은 경우 에러가 발생한다.")
    @Test
    void findShortestPathWithNotConnectedStations() {
        //given
        given(lineStations1.getStationsInOrder())
                .willReturn(Lists.list(lineStation1, lineStation2));
        given(lineStations2.getStationsInOrder())
                .willReturn(Lists.list(lineStation4, lineStation5));

        Line line1 = reflectionLine(1L, "2호선", "GREEN", lineStations1);
        Line line2 = reflectionLine(2L, "신분당선", "RED", lineStations2);


        given(lineService.findAllLineEntities())
                .willReturn(Lists.list(line1, line2));

        //when
        assertThatThrownBy(() -> pathService.findShortestPath(1L, 4L))
                //then
                .isInstanceOf(NoPathExistsException.class);

    }

    @DisplayName("최단 경로 탐색 요청 시, 존재하지 않은 출발역이나 도착역을 조회 할 경우 에러가 발생한다.")
    @Test
    void findShortestPathWithNotExistStations() {
        //given
        given(lineStations1.getStationsInOrder())
                .willReturn(Lists.list(lineStation1, lineStation2, lineStation3));
        given(lineStations2.getStationsInOrder())
                .willReturn(Lists.list(lineStation4, lineStation5));

        Line line1 = reflectionLine(1L, "2호선", "GREEN", lineStations1);
        Line line2 = reflectionLine(2L, "신분당선", "RED", lineStations2);

        given(lineService.findAllLineEntities())
                .willReturn(Lists.list(line1, line2));

        //when
        assertThatThrownBy(() -> pathService.findShortestPath(5L, 6L))
                //then
                .isInstanceOf(NotFoundException.class);
    }

    @DisplayName("최단 경로 탐색하여 최단경로를 리턴한다.")
    @Test
    void findShortestPath() {
        //given
        given(lineStations1.getStationsInOrder())
                .willReturn(Lists.list(lineStation1, lineStation2, lineStation3));
        given(lineStations2.getStationsInOrder())
                .willReturn(Lists.list(lineStation4, lineStation5));

        Line line1 = reflectionLine(1L, "2호선", "GREEN", lineStations1);
        Line line2 = reflectionLine(2L, "신분당선", "RED", lineStations2);

        StationResponse stationResponse1 = mock(StationResponse.class);
        StationResponse stationResponse2 = mock(StationResponse.class);
        StationResponse stationResponse3 = mock(StationResponse.class);
        StationResponse stationResponse4 = mock(StationResponse.class);

        given(stationResponse1.getId()).willReturn(1L);
        given(stationResponse2.getId()).willReturn(2L);
        given(stationResponse3.getId()).willReturn(3L);
        given(stationResponse4.getId()).willReturn(4L);

        given(lineService.findAllLineEntities())
                .willReturn(Lists.list(line1, line2));
        given(stationService.findAllById(anyList()))
                .willReturn(Lists.list(stationResponse1, stationResponse2, stationResponse3, stationResponse4));

        //when
        PathResponse shortestPath = pathService.findShortestPath(1L, 4L);

        //then
        assertThat(shortestPath.getStations()).hasSize(4)
                .extracting(StationResponse::getId)
                .containsExactly(1L, 2L, 3L, 4L);
        assertThat(shortestPath.getDistance()).isEqualTo(15);
        assertThat(shortestPath.getDuration()).isEqualTo(15);
    }

    @DisplayName("최단 경로 탐색하여 여러개의 최단 경로가 나와도 하나의 최단 경로만 리턴한다.")
    @Test
    void findShortestPathWithMultipleAnswers() {
        //given
        given(lineStations1.getStationsInOrder())
                .willReturn(Lists.list(lineStation1, lineStation2, lineStation3));
        given(lineStations2.getStationsInOrder())
                .willReturn(Lists.list(lineStation4, lineStation5, lineStation5));

        Line line1 = reflectionLine(1L, "2호선", "GREEN", lineStations1);
        Line line2 = reflectionLine(2L, "신분당선", "RED", lineStations2);

        StationResponse stationResponse1 = mock(StationResponse.class);
        StationResponse stationResponse2 = mock(StationResponse.class);
        StationResponse stationResponse3 = mock(StationResponse.class);
        StationResponse stationResponse4 = mock(StationResponse.class);

        given(stationResponse1.getId()).willReturn(1L);
        given(stationResponse2.getId()).willReturn(2L);
        given(stationResponse3.getId()).willReturn(3L);
        given(stationResponse4.getId()).willReturn(4L);

        given(lineService.findAllLineEntities())
                .willReturn(Lists.list(line1, line2));
        given(stationService.findAllById(anyList()))
                .willReturn(Lists.list(stationResponse1, stationResponse2, stationResponse3, stationResponse4));

        //when
        PathResponse shortestPath = pathService.findShortestPath(1L, 4L);

        //then
        assertThat(shortestPath.getStations()).hasSize(4)
                .extracting(StationResponse::getId)
                .containsExactly(1L, 2L, 3L, 4L);
        assertThat(shortestPath.getDistance()).isEqualTo(15);
        assertThat(shortestPath.getDuration()).isEqualTo(15);
    }


    private Line reflectionLine(long id, String name, String color, LineStations lineStations) {
        Line line = new Line();
        ReflectionTestUtils.setField(line, "id", id);
        ReflectionTestUtils.setField(line, "name", name);
        ReflectionTestUtils.setField(line, "color", color);
        ReflectionTestUtils.setField(line, "startTime", LocalTime.now());
        ReflectionTestUtils.setField(line, "endTime", LocalTime.now());
        ReflectionTestUtils.setField(line, "intervalTime", 4);
        ReflectionTestUtils.setField(line, "lineStations", lineStations);
        return line;
    }
}