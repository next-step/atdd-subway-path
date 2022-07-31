package nextstep.subway.unit;


import nextstep.subway.applicaion.PathService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PathServiceMockTest {
    @Mock
    LineRepository lineRepository;
    @Mock
    StationService stationService;

    Station 교대역;
    Station 강남역;
    Station 양재역;
    Station 남부터미널역;

    Line 이호선;
    Line 삼호선;
    Line 신분당선;
    List<Line> lineList;

    @BeforeEach
    void setUp() {
        교대역 = new Station(1L, "교대역");
        강남역 = new Station(2L, "강남역");
        양재역 = new Station(3L, "양재역");
        남부터미널역 = new Station(4L, "남부터미널역");

        이호선 = new Line("2호선", "green");
        삼호선 = new Line("3호선", "orange");
        신분당선 = new Line("신분당선", "red");

        이호선.addSection(교대역, 강남역, 10);
        삼호선.addSection(교대역, 남부터미널역, 10);
        신분당선.addSection(교대역, 남부터미널역, 10);
        삼호선.addSection(남부터미널역, 양재역, 3);

        lineList = Arrays.asList(이호선, 삼호선, 신분당선);
    }

    @DisplayName("경로 조회 로직")
    @Test
    void findPaths() {
        //given
        when(lineRepository.findAll()).thenReturn(lineList);
        when(stationService.findById(교대역.getId())).thenReturn(교대역);
        when(stationService.findById(양재역.getId())).thenReturn(양재역);

        //when
        PathService pathService = new PathService(stationService, lineRepository);
        PathResponse pathResponse = pathService.findPaths(교대역.getId(), 양재역.getId());

        //then
        assertThat(pathResponse.getStations()).hasSize(3);
        assertThat(pathResponse.getStations()
                .stream()
                .map(station -> station.getId())
                .collect(Collectors.toList())).containsExactly(교대역.getId(), 남부터미널역.getId(), 양재역.getId());
    }

    @DisplayName("경로 조회 로직 검증시, 출발역과 도착역이 동일하다.")
    @Test
    void findPaths_fail_source_equals_target() {
        //when & then
        PathService pathService = new PathService(stationService, lineRepository);
        assertThatThrownBy(() -> pathService.findPaths(교대역.getId(), 교대역.getId())).isInstanceOf(BusinessException.class);
    }

    @DisplayName("경로 조회 로직 검증시, 출발역이 null 값이다.")
    @Test
    void findPaths_fail_source_is_null() {
        //when & then
        PathService pathService = new PathService(stationService, lineRepository);
        assertThatThrownBy(() -> pathService.findPaths(null, 교대역.getId())).isInstanceOf(BusinessException.class);
    }
}