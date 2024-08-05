package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.jayway.jsonpath.PathNotFoundException;
import java.util.Arrays;
import java.util.List;
import nextstep.subway.acceptance.station.StationUtils;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.entity.Line;
import nextstep.subway.line.domain.entity.LineSection;
import nextstep.subway.line.domain.entity.LineSections;
import nextstep.subway.path.application.PathFinder;
import nextstep.subway.path.application.PathService;
import nextstep.subway.path.application.dto.PathResponse;
import nextstep.subway.station.application.dto.StationResponse;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.station.exception.StationNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PathServiceMockTest {

    @Mock
    private PathFinder pathFinder;

    @Mock
    private StationRepository stationRepository;

    @Mock
    private LineRepository lineRepository;

    @InjectMocks
    private PathService pathService;

    private Station 교대역;
    private Station 강남역;
    private Station 양재역;
    private Station 남부터미널역;
    private List<Line> lines;
    private Long 교대역_id = 1L;
    private Long 강남역_id = 2L;
    private Long 양재역_id = 3L;
    private Long 남부터미널역_id = 4L;
    private Long sourceId;
    private Long targetId;

    /**
     * 교대역    --- *2호선* ---   강남역 |                        | *3호선*                   *신분당선* | |
     * 남부터미널역  --- *3호선* ---   양재
     */

    @BeforeEach
    void setUp() {
        교대역 = new Station("교대역");
        강남역 = new Station("강남역");
        양재역 = new Station("양재역");
        남부터미널역 = new Station("남부터미널역");

        Line 이호선 = new Line("이호선", "bg-red-600", new LineSections());
        Line 신분당선 = new Line("신분당선", "bg-green-600", new LineSections());
        Line 삼호선 = new Line("삼호선", "bg-orange-600", new LineSections());

        이호선.addSection(new LineSection(이호선, 교대역, 강남역, 10L));
        신분당선.addSection(new LineSection(신분당선, 강남역, 양재역, 10L));
        삼호선.addSection(new LineSection(삼호선, 교대역, 남부터미널역, 2L));
        삼호선.addSection(new LineSection(삼호선, 남부터미널역, 양재역, 10L));

        lines = Arrays.asList(이호선, 신분당선, 삼호선);

        sourceId = 교대역_id;
        targetId = 양재역_id;
    }

    @Test
    @DisplayName("유효한 출발역과 도착역 ID가 주어지면 최단 경로를 반환한다")
    void it_returns_shortest_path() {
        // given
        PathResponse expectedPathResponse = new PathResponse(
            Arrays.asList(new StationResponse(교대역_id, 교대역.getName()),
                new StationResponse(남부터미널역_id, 남부터미널역.getName()),
                new StationResponse(양재역_id, 양재역.getName())), 12L);

        when(stationRepository.findByIdOrThrow(sourceId)).thenReturn(교대역);
        when(stationRepository.findByIdOrThrow(targetId)).thenReturn(양재역);
        when(lineRepository.findAll()).thenReturn(lines);
        when(pathFinder.find(lines, 교대역, 양재역)).thenReturn(expectedPathResponse);

        // when
        PathResponse actualPathResponse = pathService.findShortestPath(sourceId, targetId);

        // then
        assertThat(actualPathResponse).isEqualTo(expectedPathResponse);
        assertThat(actualPathResponse.getDistance()).isEqualTo(expectedPathResponse.getDistance());
    }

    @Test
    @DisplayName("존재하지 않는 출발역 ID가 주어지면 StationNotFoundException을 던진다")
    void it_throws_StationNotFoundException1() {
        // given
        Long nonExistentSourceId = 999L;

        when(stationRepository.findByIdOrThrow(nonExistentSourceId))
            .thenThrow(new StationNotFoundException(nonExistentSourceId));

        // when, then
        assertThatThrownBy(() -> pathService.findShortestPath(nonExistentSourceId, targetId))
            .isInstanceOf(StationNotFoundException.class)
            .hasMessageContaining(String.valueOf(nonExistentSourceId));
    }

    @Test
    @DisplayName("존재하지 않는 도착역 ID가 주어지면 StationNotFoundException을 던진다")
    void it_throws_StationNotFoundException2() {
        // given
        Long nonExistentTargetId = 999L;

        when(stationRepository.findByIdOrThrow(sourceId)).thenReturn(교대역);
        when(stationRepository.findByIdOrThrow(nonExistentTargetId))
            .thenThrow(new StationNotFoundException(nonExistentTargetId));

        // when, then
        assertThatThrownBy(() -> pathService.findShortestPath(sourceId, nonExistentTargetId))
            .isInstanceOf(StationNotFoundException.class)
            .hasMessageContaining(String.valueOf(nonExistentTargetId));
    }

    @Test
    @DisplayName("출발역과 도착역 사이에 경로가 없으면 PathNotFoundException을 던진다")
    void it_throws_PathNotFoundException_when_no_path_between_stations() {
        // given
        Station disconnectedStation = new Station("공덕역");
        Long disconnectedStation_id = 5L;

        when(stationRepository.findByIdOrThrow(sourceId)).thenReturn(교대역);
        when(stationRepository.findByIdOrThrow(disconnectedStation_id)).thenReturn(
            disconnectedStation);
        when(lineRepository.findAll()).thenReturn(lines);
        when(pathFinder.find(lines, 교대역, disconnectedStation))
            .thenThrow(new PathNotFoundException());

        // when, then
        assertThatThrownBy(() -> pathService.findShortestPath(sourceId, disconnectedStation_id))
            .isInstanceOf(PathNotFoundException.class);
    }
}
