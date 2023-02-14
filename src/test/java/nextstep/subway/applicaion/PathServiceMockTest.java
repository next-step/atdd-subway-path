package nextstep.subway.applicaion;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("구간 관련 기능")
@ExtendWith(value = MockitoExtension.class)
class PathServiceMockTest {

    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;

    @InjectMocks
    private PathService pathService;

    private Line 삼호선;
    private Line 이호선;
    private Line 신분당선;
    private Line 수인분당선;

    private Station 남부터미널역;
    private Station 교대역;
    private Station 강남역;
    private Station 양재역;
    private Station 정자역;

    @BeforeEach
    void setUp() {
        삼호선 = new Line("3호선", "bg-orange-500");
        이호선 = new Line("2호선", "bg-green-500");
        신분당선 = new Line("신분당선", "bg-red-500");
        수인분당선 = new Line("수인분당선", "bg-yellow-500");

        남부터미널역 = new Station("남부터미널역");
        교대역 = new Station("교대역");
        강남역 = new Station("강남역");
        양재역 = new Station("양재역");
        정자역 = new Station("정자역");
    }

    /**
     * 교대역   --- *2호선* ---   강남역
     * |                        |
     * *3호선*                   *신분당선*
     * |                        |
     * 남부터미널역 --- *3호선* --- 양재역  --- *수인분당선* ---  정자역
     */
    @DisplayName("출발역과 도착역을 기준으로 최단 경로를 반환한다.")
    @Test
    void findPath() {
        // given
        삼호선.addSection(new Section(삼호선, 교대역, 남부터미널역, 1));
        삼호선.addSection(new Section(삼호선, 남부터미널역, 양재역, 100));
        이호선.addSection(new Section(이호선, 교대역, 강남역, 4));
        신분당선.addSection(new Section(신분당선, 강남역, 양재역, 5));
        수인분당선.addSection(new Section(수인분당선, 양재역, 정자역, 9));
        List<StationResponse> stations = Stream.of(강남역, 양재역, 정자역)
                .map(station -> new StationResponse(station.getId(), station.getName()))
                .collect(Collectors.toUnmodifiableList());

        when(stationService.findById(1L)).thenReturn(강남역);
        when(stationService.findById(2L)).thenReturn(정자역);
        when(lineRepository.findAll()).thenReturn(List.of(이호선, 삼호선, 신분당선, 수인분당선));
        when(stationService.createStationResponsesBy(anyList())).thenReturn(stations);

        // when
        PathResponse path = pathService.findPathBy(1L, 2L);

        // then
        assertAll(
                () -> assertThat(path.getDistance()).isEqualTo(14),
                () -> assertThat(path.getStations()).extracting("name")
                        .containsExactly("강남역", "양재역", "정자역")
        );
    }
}
