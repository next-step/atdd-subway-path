package nextstep.subway.path.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.path.dto.PathResponse;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
@Transactional
public class PathServiceTest {

    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private PathService pathService;

    private Station 강남역;
    private Station 교대역;
    private Station 삼성역;
    private Station 고속터미널역;
    private Station 야탑역;
    private Station 판교역;

    private Line 이호선;
    private Line 삼호선;
    private Line 분당선;

    @BeforeEach
    void setUp() {
        강남역 = stationRepository.save(new Station("강남역"));
        교대역 = stationRepository.save(new Station("교대역"));
        삼성역 = stationRepository.save(new Station("삼성역"));
        고속터미널역 = stationRepository.save(new Station("고속터미널역"));

        이호선 = lineRepository.save(new Line("이호선", "green", 삼성역, 강남역, 10));
        이호선.addSection(강남역, 교대역, 10);
        삼호선 = lineRepository.save(new Line("삼호선", "red", 교대역, 고속터미널역, 10));
    }

    @DisplayName("최단거리 조회")
    @Test
    void getPath() {
        // when
        PathResponse paths = pathService.findPaths(삼성역.getId(), 고속터미널역.getId());

        // then
        List<Long> expectedStationIds = Stream.of(삼성역, 강남역, 교대역, 고속터미널역)
                .map(Station::getId)
                .collect(Collectors.toList());

        List<Long> pathStationIds = paths.getStations().stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());

        assertThat(paths.getStations()).hasSize(4);
        assertThat(paths.getDistance()).isEqualTo(30);
        assertThat(pathStationIds).containsExactlyElementsOf(expectedStationIds);
    }

    @DisplayName("출발역과 도착역이 같은 경우")
    @Test
    void failSearchPathSameStations() {

        // then
        assertThatThrownBy(() -> pathService.findPaths(삼성역.getId(), 삼성역.getId()))
                .isInstanceOf(RuntimeException.class);
    }

    @DisplayName("출발역과 도착역이 연결이 되어 있지 않은 경우")
    @Test
    void failSearchPathNotMatchStations() {

        // given
        판교역 = stationRepository.save(new Station("판교역"));
        야탑역 = stationRepository.save(new Station("야탑역"));
        분당선 = lineRepository.save(new Line("분당선", "yellow", 판교역, 야탑역, 10));

        // then
        assertThatThrownBy(() -> pathService.findPaths(삼성역.getId(), 판교역.getId()))
                .isInstanceOf(RuntimeException.class);
    }

    @DisplayName("존재하지 않은 출발역이나 도착역을 조회 할 경우")
    @Test
    void failSearchPathNoSearchStations() {

        // then
        assertThatThrownBy(() -> pathService.findPaths(1000L, 삼성역.getId()))
                .isInstanceOf(RuntimeException.class);
    }
}
