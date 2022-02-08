package nextstep.subway.unit;

import nextstep.subway.applicaion.PathService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PathServiceTest {

    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;

    private PathService pathService;

    Station 교대역;
    Station 강남역;
    Station 양재역;
    Station 남부터미널역;

    Line 이호선;
    Line 삼호선;
    Line 신분당선;

    Section 교대역_강남역_구간;
    Section 교대역_남부터미널_구간;
    Section 남부터미널_양재역_구간;
    Section 강남역_양재역_구간;

    @BeforeEach
    void setUp() {
        교대역 = new Station("교대역");
        강남역 = new Station("강남역");
        양재역 = new Station("양재역");
        남부터미널역 = new Station("남부터미널역");

        이호선 = new Line("2호선", "green");
        삼호선 = new Line("3호선", "orange");
        신분당선 = new Line("신분당선", "red");

        교대역_강남역_구간 = new Section(이호선, 교대역, 강남역, 10);
        교대역_남부터미널_구간 = new Section(삼호선, 교대역, 남부터미널역, 2);
        남부터미널_양재역_구간 = new Section(삼호선, 남부터미널역, 양재역, 3);
        강남역_양재역_구간 = new Section(신분당선, 강남역, 양재역, 10);

        이호선.addSection(교대역_강남역_구간);
        삼호선.addSection(교대역_남부터미널_구간);
        삼호선.addSection(남부터미널_양재역_구간);
        신분당선.addSection(강남역_양재역_구간);
        pathService = new PathService(lineRepository, stationService);
    }

    @Test
    void findPath() {
        // given
        when(lineRepository.findAll()).thenReturn(Arrays.asList(이호선, 삼호선, 신분당선));
        when(stationService.findById(1L)).thenReturn(교대역);
        when(stationService.findById(3L)).thenReturn(양재역);

        // when
        PathResponse response = pathService.findPath(1L, 3L);

        // then
        assertThat(response.getStations()).containsExactly(
                StationResponse.createStationResponse(교대역),
                StationResponse.createStationResponse(남부터미널역),
                StationResponse.createStationResponse(양재역));
        assertThat(response.getDistance()).isEqualTo(5);
    }
}
