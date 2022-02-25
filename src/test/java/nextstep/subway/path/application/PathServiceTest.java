package nextstep.subway.path.application;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.path.dto.PathResponse;
import nextstep.subway.path.exception.InvalidSourceTargetException;
import nextstep.subway.path.exception.PathNotFoundException;
import nextstep.subway.path.exception.SourceTargetNotFoundException;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@Transactional
class PathServiceTest {
    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private LineRepository lineRepository;

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
        교대역 = stationRepository.save(new Station("교대역"));
        강남역 = stationRepository.save(new Station("강남역"));
        양재역 = stationRepository.save(new Station("양재역"));
        남부터미널역 = stationRepository.save(new Station("남부터미널역"));

        이호선 = lineRepository.save(new Line("2호선", "green", 교대역, 강남역, 10));
        신분당선 = lineRepository.save(new Line("신분당선", "red", 강남역, 양재역, 10));
        삼호선 = lineRepository.save(new Line("3호선", "green", 교대역, 남부터미널역, 2));

        pathService = new PathService(stationRepository, lineRepository);
    }

    @DisplayName("출발역부터 도착역까지의 최단 경로를 조회할 수 있다.")
    @Test
    void findShortestPath() {
        // when
        PathResponse path = pathService.findShortestPath(교대역.getId(), 강남역.getId());
        List<String> actualStations = path.getStations()
                .stream()
                .map(StationResponse::getName)
                .collect(Collectors.toList());
        List<String> expectedStations = Arrays.asList("교대역", "강남역");

        // then
        assertThat(actualStations).isEqualTo(expectedStations);
    }

    @DisplayName("출발역과 도착역이 같은 경우 최단 경로 조회에 실패한다.")
    @Test
    void sameSourceAndTarget() {
        assertThatExceptionOfType(InvalidSourceTargetException.class)
                .isThrownBy(() -> pathService.findShortestPath(강남역.getId(), 강남역.getId()));
    }

    @DisplayName("출발역과 도착역이 연결이 되어 있지 않은 경우 최단 경로 조회에 실패한다.")
    @Test
    void pathNotConnected() {
        // given
        Station 사당역 = stationRepository.save(new Station("사당역"));
        Station 이수역 = stationRepository.save(new Station("이수역"));
        Line 사호선 = lineRepository.save(new Line("4호선", "blue", 사당역, 이수역, 2));

        // when, then
        assertThatExceptionOfType(PathNotFoundException.class)
                .isThrownBy(() -> pathService.findShortestPath(강남역.getId(), 사당역.getId()));
    }

    @DisplayName("존재하지 않은 출발역이나 도착역을 조회 할 경우 최단 경로 조회에 실패한다.")
    @Test
    void pathNotFound() {
        // given
        Long stationId = Long.MAX_VALUE;

        // when, then
        assertAll(
                () -> assertThatExceptionOfType(SourceTargetNotFoundException.class)
                        .isThrownBy(() -> pathService.findShortestPath(강남역.getId(), stationId)),
                () -> assertThatExceptionOfType(SourceTargetNotFoundException.class)
                        .isThrownBy(() -> pathService.findShortestPath(stationId, 강남역.getId()))
        );
    }
}
