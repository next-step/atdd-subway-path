package nextstep.subway.unit.application;

import nextstep.subway.applicaion.PathService;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.ShortestPathResult;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.PathFinder;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class PathServiceMockTest {
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationRepository stationRepository;
    @Mock
    private PathFinder pathFinder;
    @InjectMocks
    private PathService pathService;

    private Station 교대역;
    private Station 강남역;
    private Station 양재역;
    private Station 남부터미널역;
    private Line 이호선;
    private Line 신분당선;
    private Line 삼호선;
    private List<Line> lines;

    /**
     * 교대역    --- *2호선* ---   강남역
     * |                        |
     * *3호선*                   *신분당선*
     * |                        |
     * 남부터미널역  --- *3호선* ---   양재
     */
    @BeforeEach
    public void setUp() {
        교대역 = new Station(11L, "교대역");
        강남역 = new Station(12L, "강남역");
        양재역 = new Station(13L, "양재역");
        남부터미널역 = new Station(14L, "남부터미널역");

        이호선 = new Line("이호선", "red");
        신분당선 = new Line("신분당선", "yellow");
        삼호선 = new Line("삼호선", "green");

        이호선.addSection(교대역, 강남역, 10);
        신분당선.addSection(강남역, 양재역, 10);
        삼호선.addSection(교대역, 남부터미널역, 2);
        삼호선.addSection(남부터미널역, 양재역, 3);

        lines = new ArrayList<>();
        lines.addAll(List.of(이호선, 신분당선, 삼호선));
    }

    @Test
    void 최소경로_조회() {
        // given
        final ShortestPathResult shortestPathResult = new ShortestPathResult(5, List.of(교대역, 남부터미널역, 양재역));
        given(stationRepository.findById(교대역.getId())).willReturn(Optional.ofNullable(교대역));
        given(stationRepository.findById(양재역.getId())).willReturn(Optional.ofNullable(양재역));
        given(lineRepository.findAll()).willReturn(List.of(이호선, 신분당선, 삼호선));
        given(pathFinder.calShortestPath(lines, 교대역, 양재역)).willReturn(shortestPathResult);

        // when
        PathResponse response = pathService.getShortestPath(교대역.getId(), 양재역.getId());

        // then
        최소경로_순서_고려하여_검증(response,"교대역", "남부터미널역", "양재역");
        assertThat(response.getDistance()).isEqualTo(5);
    }

    private void 최소경로_순서_고려하여_검증(final PathResponse response, String... names) {
        List<String> stationNames = createStationNames(response);
        assertThat(response.getStations().size()).isEqualTo(names.length);
        assertThat(stationNames).containsExactly(names);
    }

    private List<String> createStationNames(final PathResponse response) {
        return response.getStations().stream()
                       .map(StationResponse::getName)
                       .collect(Collectors.toList());
    }
}