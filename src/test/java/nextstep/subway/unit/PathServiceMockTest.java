package nextstep.subway.unit;

import com.google.common.collect.Lists;
import nextstep.subway.applicaion.PathService;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PathServiceMockTest {
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationRepository stationRepository;

    private PathService pathService = null;

    private Station 교대역;
    private Station 강남역;
    private Station 양재역;
    private Station 남부터미널역;

    private Line 이호선;
    private Line 신분당선;
    private Line 삼호선;

    private List<Station> stations = new ArrayList<>();
    private List<Line> lines = new ArrayList<>();

    @BeforeEach
    public void setUp() {
        교대역 = new Station(1, "교대역");
        강남역 = new Station(2, "강남역");
        양재역 = new Station(3, "양재역");
        남부터미널역 = new Station(4, "남부터미널역");

        이호선 = new Line("2호선", "red");
        신분당선 = new Line("신분당선", "red");
        삼호선 = new Line("3호선", "red");

        이호선.addSection(교대역, 강남역, 10);
        신분당선.addSection(강남역, 양재역, 10);
        삼호선.addSection(교대역, 남부터미널역, 5);
        삼호선.addSection(남부터미널역, 양재역, 4);

        stations.add(교대역);
        stations.add(강남역);
        stations.add(양재역);
        stations.add(남부터미널역);

        lines.add(이호선);
        lines.add(신분당선);
        lines.add(삼호선);

        pathService = new PathService(lineRepository, stationRepository);
    }

    @DisplayName("최단 경로를 조회")
    @Test
    public void getPathsStation() {
        // given
        List<Line> lines = Lists.newArrayList(신분당선, 이호선, 삼호선);

        when(lineRepository.findAll()).thenReturn(lines);
        when(stationRepository.findAll()).thenReturn(stations);

        // when
        PathResponse response = pathService.showPaths(교대역.getId(), 양재역.getId());

        // then
        List<Long> expected = Lists.newArrayList(교대역.getId(), 남부터미널역.getId(), 양재역.getId());
        assertThat(response.getStations()).hasSize(3)
                .extracting(StationResponse::getId)
                .containsExactlyElementsOf(expected);
    }

    @DisplayName("존재하지 않은 출발역이나 도착역을 조회 할 경우")
    @Test
    public void getPathsStationError2() {
        // given
        List<Line> lines = Lists.newArrayList(신분당선, 이호선, 삼호선);

        when(lineRepository.findAll()).thenReturn(lines);
        when(stationRepository.findAll()).thenReturn(stations);

        // when
        assertThatThrownBy(() -> pathService.showPaths(100L, 양재역.getId()))
                .isInstanceOf(RuntimeException.class);
    }
}
