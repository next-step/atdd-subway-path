package nextstep.subway.application;

import nextstep.subway.applicaion.PathService;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest
@Transactional
public class PathServiceMockTest {

    @Autowired
    private PathService pathService;
    @MockBean
    private StationRepository stationRepository;
    @MockBean
    private LineRepository lineRepository;
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

        이호선 = new Line("2호선", "green");
        이호선.addSection(교대역, 강남역, 10);
        신분당선 = new Line("신분당선", "red");
        신분당선.addSection(강남역, 양재역, 10);
        삼호선 = new Line("3호선", "orange");
        삼호선.addSection(교대역, 남부터미널역, 2);
        삼호선.addSection(남부터미널역, 양재역, 3);
    }

    @DisplayName("최단 경로 조회")
    @Test
    void findShortestPath() {
        // given
        when(stationRepository.findById(교대역.getId())).thenReturn(Optional.of(교대역));
        when(stationRepository.findById(양재역.getId())).thenReturn(Optional.of(양재역));
        when(lineRepository.findAll()).thenReturn(Lists.newArrayList(이호선, 신분당선, 삼호선));

        // when
        final PathResponse pathResponse = pathService.findShortestPath(교대역.getId(), 양재역.getId());

        // then
        final List<Long> stationIds = pathResponse.getStations().stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());
        assertThat(stationIds).containsExactly(교대역.getId(), 남부터미널역.getId(), 양재역.getId());
        assertThat(pathResponse.getDistance()).isEqualTo(5);
    }
}
