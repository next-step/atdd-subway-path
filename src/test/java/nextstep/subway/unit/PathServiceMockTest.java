package nextstep.subway.unit;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.path.application.PathService;
import nextstep.subway.path.dto.PathResponse;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PathServiceMockTest {
    @Mock
    private StationRepository stationRepository;

    @Mock
    private LineRepository lineRepository;

    private PathService pathService;

    @BeforeEach
    void setUp() {
        pathService = new PathService(stationRepository, lineRepository);
    }

    /*
     * # 예외
     * - 출발역과 도착역이 같은 경우
     * - 출발역과 도착역이 연결이 되어 있지 않은 경우
     * - 존재하지 않은 출발역이나 도착역을 조회 할 경우
     */
    @DisplayName("출발역에서 도착역까지의 최단경로를 탐색")
    @Test
    void searchPath() {
        // given: 역 정보와 노선 정보가 주어진다.
        Station 교대역 = new Station(1L, "교대역");
        Station 강남역 = new Station(2L, "강남역");
        Station 양재역 = new Station(3L, "양재역");
        Station 남부터미널역 = new Station(4L, "남부터미널역");

        Line 이호선 = new Line("2호선", "green", new Section(교대역, 강남역, 10));
        Line 신분당선 = new Line("신분당선", "red", new Section(강남역, 양재역, 10));
        Line 삼호선 = new Line("3호선", "orange", new Section(교대역, 남부터미널역, 2));

        삼호선.registerSection(new Section(남부터미널역, 양재역, 3));

        when(stationRepository.findById(anyLong()))
                .thenReturn(Optional.of(교대역))   // 출발역
                .thenReturn(Optional.of(양재역));  // 도착역

        when(lineRepository.findAll())
                .thenReturn(List.of(이호선, 신분당선, 삼호선));

        // when: 출발역 id와 도착역 id를 받으면 최단경로를 반환한다.
        PathResponse pathResponse = pathService.searchPath(1L, 2L);
        StationResponse response1 = StationResponse.from(교대역);
        StationResponse response2 = StationResponse.from(남부터미널역);
        StationResponse response3 = StationResponse.from(양재역);

        // then: 최단경로는 아래의 결과를 가져야 한다.
        assertThat(pathResponse.getStations()).containsExactly(response1, response2, response3);
        assertThat(pathResponse.getDistance()).isEqualTo(5);
    }
}
