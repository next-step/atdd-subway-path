package nextstep.subway.unit;

import nextstep.subway.applicaion.PathService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PathServiceMockTest {
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;

    private Station 교대역;
    private Station 강남역;
    private Station 양재역;
    private Station 남부터미널역;
    private Line 이호선;
    private Section 이호선라인;
    private Line 신분당선;
    private Section 신분당선라인;
    private Line 삼호선;
    private Section 삼호선라인;
    private Section 삼호선라인2;

    /**
     * 교대역    --- *2호선* ---   강남역
     * |                        |
     * *3호선*                   *신분당선*
     * |                        |
     * 남부터미널역  --- *3호선* ---   양재
     */
    @BeforeEach
    public void setUp() {

        교대역 = new Station(1L, "교대역");
        강남역 = new Station(2L, "강남역");
        양재역 = new Station(3L, "양재역");
        남부터미널역 = new Station(4L, "남부터미널역");

        이호선 = new Line(1L, "2호선", "green");
        신분당선 = new Line(2L, "신분당선", "red");
        삼호선 = new Line(3L, "3호선", "orange");

        이호선라인 = new Section(이호선, 1L, 교대역, 강남역, 10);
        이호선.addSection(이호선라인);
        신분당선라인 = new Section(신분당선, 2L, 강남역, 양재역, 10);
        신분당선.addSection(신분당선라인);
        삼호선라인 = new Section(삼호선, 3L, 교대역, 남부터미널역, 2);
        삼호선.addSection(삼호선라인);
        삼호선라인2 = new Section(삼호선, 4L, 남부터미널역, 양재역, 3);
        삼호선.addSection(삼호선라인2);

    }

    /**
     * Given 출발역과 도착역, 노선들과 구간들의 정보가 주어졌을 때
     * When 범위내의 출발역과 도착역을 입력하면
     * Then 출발역과 도착역 사이 경로와 최단거리를 알 수 있다.
     */
    @Test
    void showPaths() {

        //given
        when(stationService.findById(1L)).thenReturn(교대역);
        when(stationService.findById(3L)).thenReturn(양재역);
        when(lineRepository.findAll()).thenReturn(List.of(이호선, 신분당선, 삼호선));
        PathService pathService = new PathService(lineRepository, stationService);
        SubwayGraph graph = new SubwayGraph(List.of(이호선, 신분당선, 삼호선));

        //when
        pathService.showPaths(교대역.getId(), 양재역.getId());

        //then
        assertThat(graph.getShortestPath(교대역, 양재역)).contains(교대역, 양재역);
        assertThat(graph.getShortestDistance(교대역, 양재역)).isEqualTo(5);

    }

    /**
     * When 출발역과 도착역을 같은 역을 입력하면
     * Then 연결되어 있지 않아서 조회할 수 없다.
     */

    @DisplayName("출발역과 도착역이 같으면 조회할 수 없다")
    @Test
    void canNotSearchWhenSourceTargetIsSame() {
        //given
        when(stationService.findById(1L)).thenReturn(교대역);
        when(stationService.findById(1L)).thenReturn(교대역);
        when(lineRepository.findAll()).thenReturn(List.of(이호선, 신분당선, 삼호선));
        PathService pathService = new PathService(lineRepository, stationService);

        //When Then
        assertThatThrownBy(() -> pathService.showPaths(교대역.getId(), 교대역.getId()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    /**
     * Given 연결되어 있지 않는 지하철 노선과 구간, 역을 생성하고
     * When 서로 연결되어 있지 않은 출발역과 도착역을 입력하면
     * Then 연결되어 있지 않아서 조회할 수 없다.
     */

    @DisplayName("출발역과 도착역이 연결되어 있지 않으면 조회할 수 없다")
    @Test
    void canNotSearchWhenSourceTargetNotLinked() {
        //given
        Station 새로운역1 = new Station(11L, "새로운역1");
        Station 새로운역2 = new Station(22L, "새로운역2");
        when(stationService.findById(1L)).thenReturn(교대역);
        when(stationService.findById(22L)).thenReturn(새로운역2);
        Line 새로운노선 = new Line(33L, "새로운 노선", "new");
        새로운노선.addSection(new Section(새로운노선, 33L, 새로운역1, 새로운역2, 20));
        when(lineRepository.findAll()).thenReturn(List.of(이호선, 신분당선, 삼호선, 새로운노선));
        PathService pathService = new PathService(lineRepository, stationService);

        //When Then
        assertThatThrownBy(() -> pathService.showPaths(교대역.getId(), 새로운역2.getId()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    /**
     * Given 노선에 없는 역을 생성하고
     * When 노선에 없는 역을 포함하여 입력하면
     * Then 노선에 존재하지 않기 때문에 조회할 수 없다.
     */

    @DisplayName("출발역과 도착역이 존재하지 않으면 조회할 수 없다.")
    @Test
    void canNotSearchWhenSourceTargetNotFound() {
        //given
        when(stationService.findById(1L)).thenReturn(교대역);
        when(stationService.findById(99L)).thenReturn(new Station(99L, "새로운역"));
        when(lineRepository.findAll()).thenReturn(List.of(이호선, 신분당선, 삼호선));
        PathService pathService = new PathService(lineRepository, stationService);

        //When Then
        assertThatThrownBy(() -> pathService.showPaths(교대역.getId(), new Station(99L, "새로운역").getId()))
                .isInstanceOf(IllegalArgumentException.class);

    }
}
