package nextstep.subway.unit;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.path.application.PathService;
import nextstep.subway.path.dto.PathResponse;
import nextstep.subway.path.exception.PathNotFoundException;
import nextstep.subway.path.exception.SameSourceAndTargetStationException;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.station.dto.StationResponse;
import nextstep.subway.station.exception.StationNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PathServiceMockTest {
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
    private Line 이호선;
    private Line 신분당선;
    private Line 삼호선;

    @BeforeEach
    void setUp() {
        교대역 = new Station(1L, "교대역");
        강남역 = new Station(2L, "강남역");
        양재역 = new Station(3L, "양재역");
        남부터미널역 = new Station(4L, "남부터미널역");

        이호선 = new Line("2호선", "green", new Section(교대역, 강남역, 10));
        신분당선 = new Line("신분당선", "red", new Section(강남역, 양재역, 10));
        삼호선 = new Line("3호선", "orange", new Section(교대역, 남부터미널역, 2));

        삼호선.registerSection(new Section(남부터미널역, 양재역, 3));
    }

    /**
     * 교대역    --- *2호선(10)* ---   강남역
     * |                        |
     * *3호선(2)*                   *신분당선(10)*
     * |                        |
     * 남부터미널역  --- *3호선(3)* ---   양재
     */
    @DisplayName("출발역에서 도착역까지의 최단경로를 탐색")
    @Test
    void searchPath() {
        // given: 역 정보와 노선 정보가 주어진다.
        when(stationRepository.findById(anyLong()))
                .thenReturn(Optional.of(교대역))   // 출발역
                .thenReturn(Optional.of(양재역));  // 도착역

        when(lineRepository.findAll())
                .thenReturn(List.of(이호선, 신분당선, 삼호선));

        // when: 출발역 id와 도착역 id를 받으면 최단경로를 반환한다.
        PathResponse pathResponse = pathService.searchPath(1L, 2L);
        List<String> stationNames = pathResponse.getStations().stream()
                .map(StationResponse::getName)
                .collect(Collectors.toList());

        // then: 최단경로는 아래의 결과를 가져야 한다.
        assertThat(stationNames).containsExactly("교대역", "남부터미널역", "양재역");
        assertThat(pathResponse.getDistance()).isEqualTo(5);
    }

    /*
     * # 예외
     * - 출발역과 도착역이 같은 경우
     * - 출발역과 도착역이 연결이 되어 있지 않은 경우
     * - 존재하지 않은 출발역이나 도착역을 조회 할 경우
     */
    @DisplayName("출발역과 도착역이 같은 경우")
    @Test
    void searchPathFailBySameStations() {
        // given
        Long source = 1L;
        Long target = 1L;

        // when, then
        assertThatThrownBy(() -> pathService.searchPath(source, target))
                .isInstanceOf(SameSourceAndTargetStationException.class);
    }

    @DisplayName("출발역과 도착역이 연결이 되어 있지 않은 경우")
    @Test
    void searchPathFailByNotHavePath() {
        // given: 역 정보와 노선 정보가 주어진다.
        Station 증미역 = new Station(5L, "증미역");
        Station 여의도역 = new Station(6L, "여의도역");

        Line 구호선 = new Line("9호선", "brown", new Section(증미역, 여의도역, 2));

        when(stationRepository.findById(anyLong()))
                .thenReturn(Optional.of(교대역))   // 출발역
                .thenReturn(Optional.of(여의도역));  // 도착역

        when(lineRepository.findAll())
                .thenReturn(List.of(이호선, 신분당선, 삼호선, 구호선));

        // when, then
        assertThatThrownBy(() -> pathService.searchPath(1L, 2L))
                .isInstanceOf(PathNotFoundException.class);
    }

    @DisplayName("존재하지 않는 출발역이나 도착역을 조회할 경우")
    @Test
    void searchPathFailByNotFoundStation() {
        // given
        when(stationRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> pathService.searchPath(1L, 2L))
                .isInstanceOf(StationNotFoundException.class);
    }
}
