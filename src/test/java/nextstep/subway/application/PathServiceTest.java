package nextstep.subway.application;

import nextstep.subway.applicaion.PathService;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.error.ErrorCode;
import nextstep.subway.error.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
public class PathServiceTest {

    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private LineRepository lineRepository;
    @Autowired
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
        교대역 = createStation("교대역");
        강남역 = createStation("강남역");
        양재역 = createStation("양재역");
        남부터미널역 = createStation("남부터미널역");

        이호선 = createLine("2호선", "green");
        이호선.addSection(교대역, 강남역, 10);
        신분당선 = createLine("신분당선", "red");
        신분당선.addSection(강남역, 양재역, 10);
        삼호선 = createLine("3호선", "orange");
        삼호선.addSection(교대역, 남부터미널역, 2);
        삼호선.addSection(남부터미널역, 양재역, 3);
    }

    @DisplayName("최단 경로 조회")
    @Test
    void findShortestPath() {
        // when
        final PathResponse pathResponse = pathService.findShortestPath(교대역.getId(), 양재역.getId());

        // then
        final List<Long> stationIds = pathResponse.getStations().stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());
        assertThat(stationIds).containsExactly(교대역.getId(), 남부터미널역.getId(), 양재역.getId());
        assertThat(pathResponse.getDistance()).isEqualTo(5);
    }

    @DisplayName("출발역과 도착역이 같을 경우 에러 발생")
    @Test
    void cannotFindShortestPathWhenSourceAndTargetIsSame() {
        // when, then
        assertThatThrownBy(() -> {
            pathService.findShortestPath(교대역.getId(), 교대역.getId());
        }).isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.SOURCE_AND_TARGET_STATION_IS_SAME.getMessage());
    }
    
    @DisplayName("출발역과 도착역이 연결되어있지 않을 경우 에러 발생j")
    @Test
    void cannotFindShortestPathWhenSourceAndTargetIsNotConnected() {
        // given
        lineRepository.delete(신분당선);
        삼호선.removeSection(교대역);

        // when, then
        assertThatThrownBy(() -> {
            pathService.findShortestPath(교대역.getId(), 양재역.getId());
        }).isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.SOURCE_AND_TARGET_STATION_IS_NOT_CONNECTED.getMessage());
    }

    @DisplayName("존재하지 않은 출발역이나 도착역을 조회할 경우 에러 발생")
    @Test
    void cannotFindShortestPathWhenSourceAndTargetIsNotExists() {
        // given
        final Station 선유도역 = createStation("선유도역");

        // when, then
        assertThatThrownBy(() -> {
            pathService.findShortestPath(선유도역.getId(), 양재역.getId());
        }).isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.STATION_NOT_FOUND.getMessage());
    }

    private Station createStation(final String name) {
        return stationRepository.save(new Station(name));
    }

    private Line createLine(final String name, final String color) {
        return lineRepository.save(new Line("2호선", "green"));
    }
}
