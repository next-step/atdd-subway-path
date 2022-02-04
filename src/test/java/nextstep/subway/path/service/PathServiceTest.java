package nextstep.subway.path.service;

import nextstep.subway.common.exception.BadRequestException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.repository.LineRepository;
import nextstep.subway.path.domain.PathFinder;
import nextstep.subway.path.dto.PathResponse;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;
import nextstep.subway.station.exception.StationNotFoundException;
import nextstep.subway.station.repository.StationRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class PathServiceTest {

    static final Long 교대역_코드 = 1L;
    static final Long 강남역_코드 = 2L;
    static final Long 남부터미널역_코드 = 3L;
    static final Long 양재역_코드 = 4L;
    static final Long 부천역_코드 = 5L;
    static final Long 역곡역_코드 = 6L;

    @Mock
    StationRepository stationRepository;

    @Mock
    LineRepository lineRepository;

    @InjectMocks
    PathService pathService;

    Station 교대역;
    Station 강남역;
    Station 남부터미널역;
    Station 양재역;
    Station 부천역;
    Station 역곡역;

    Line 이호선;
    Line 삼호선;
    Line 신분당선;
    Line 일호선;

    /**
     * 교대역   --- *2호선(길이:10)* ---    강남역
     * |                                    |
     * *3호선(길이:2)*                *신분당선(길이:10)*
     * |                                    |
     * 남부터미널역  --- *3호선(길이:3)* --- 양재
     *
     * 부천역 --- *1호선(길이:5)* --- 역곡역
     */
    @BeforeEach
    void setUp() {
        교대역 = new Station(교대역_코드, "교대역");
        강남역 = new Station(강남역_코드, "강남역");
        남부터미널역 = new Station(남부터미널역_코드, "남부터미널역");
        양재역 = new Station(양재역_코드, "양재역");
        부천역 = new Station(부천역_코드, "부천역");
        역곡역 = new Station(역곡역_코드, "역곡역");

        when(stationRepository.findAll()).thenReturn(Arrays.asList(교대역, 강남역, 남부터미널역, 양재역, 부천역, 역곡역));

        일호선 = Line.builder()
                .name("1호선")
                .color("blue")
                .build();
        일호선.addSection(부천역, 역곡역, 5);

        이호선 = Line.builder()
                .name("2호선")
                .color("green")
                .build();
        이호선.addSection(교대역, 강남역, 10);

        삼호선 = Line.builder()
                .name("3호선")
                .color("orange")
                .build();
        삼호선.addSection(교대역, 남부터미널역, 2);
        삼호선.addSection(남부터미널역, 양재역, 3);

        신분당선 = Line.builder()
                .name("신분당선")
                .color("red")
                .build();
        신분당선.addSection(강남역, 양재역, 10);

        when(lineRepository.findAll()).thenReturn(Arrays.asList(일호선, 이호선, 삼호선, 신분당선));
    }

    @Test
    void 출발역부터_도착역까지_최단경로_조회() {
        // given
        when(stationRepository.findById(교대역_코드)).thenReturn(Optional.of(교대역));
        when(stationRepository.findById(양재역_코드)).thenReturn(Optional.of(양재역));

        // when
        PathResponse response = pathService.findShortestPath(교대역.getId(), 양재역.getId());

        // then
        List<Long> stationIds = response.getStations().stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());
        assertThat(stationIds).containsExactly(교대역.getId(), 남부터미널역.getId(), 양재역.getId());
    }

    // 이 부분은 PathFinderTest에 있으므로 이제 필요하지 않다고 느껴짐
    @Test
    void 출발역_도착역_같은경우() {
        // given
        when(stationRepository.findById(부천역_코드)).thenReturn(Optional.of(부천역));

        // when, then
        Assertions.assertThatThrownBy(() ->
                        pathService.findShortestPath(부천역_코드, 부천역_코드))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(PathFinder.SAME_STATION_MESSAGE);
    }

    // 이 부분은 PathFinderTest로 검증을 할 수 없으므로 serviceTest에서 검증할 테스트로 보임
    @Test
    void 등록되지_않은_역을_조회하는_경우() {
        // given
        Station 구로역 = new Station(7L, "구로역");
        when(stationRepository.findById(부천역_코드)).thenReturn(Optional.of(부천역));

        // when, then
        Assertions.assertThatThrownBy(() ->
                        pathService.findShortestPath(부천역_코드, 구로역.getId()))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(StationNotFoundException.MESSAGE + 구로역.getId());
    }

    // 이 부분은 PathFinderTest에 있으므로 이제 필요하지 않다고 느껴짐
    @Test
    void 출발역_도착역이_연결되지않은_경우() {
        // given
        when(stationRepository.findById(부천역_코드)).thenReturn(Optional.of(부천역));
        when(stationRepository.findById(양재역_코드)).thenReturn(Optional.of(양재역));

        // when, then
        Assertions.assertThatThrownBy(() ->
                pathService.findShortestPath(부천역_코드, 양재역_코드))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(PathFinder.NON_EXIST_PATH_MESSAGE);
    }

}
