package nextstep.subway.path.application;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.exception.NotFoundException;
import nextstep.subway.path.dto.PathResponse;
import nextstep.subway.path.exception.FirstStationEqualsFinalStationException;
import nextstep.subway.path.exception.NotConnectedException;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("지하철 경로 조회 단위 테스트")
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
    private Station 남부터미널역;
    private Station 양재역;
    private Station 교대역;
    private Line 신분당선;
    private Line 이호선;
    private Line 삼호선;

    @BeforeEach
    void setUp() {
        교대역 = stationRepository.save(new Station("교대역"));
        강남역 = stationRepository.save(new Station("강남역"));
        양재역 = stationRepository.save(new Station("양재역"));
        남부터미널역 = stationRepository.save(new Station("남부터미널역"));

        신분당선 = lineRepository.save(new Line("신분당선", "red", 강남역, 양재역, 3));
        이호선 = lineRepository.save(new Line("이호선", "green", 교대역, 강남역, 5));
        삼호선 = lineRepository.save(new Line("삼호선", "orange", 교대역, 남부터미널역, 8));
    }

    @DisplayName("출발역부터 도착역까지 최단 경로 조회")
    @Test
    void shortPath() {
        PathResponse path = pathService.findShortestPath(강남역.getId(), 남부터미널역.getId());
        assertThat(path.getDistance()).isEqualTo(13);

        List<StationResponse> stationResponses =
                Arrays.asList(StationResponse.of(강남역), StationResponse.of(교대역), StationResponse.of(남부터미널역));
        assertThat(path.getStations()).containsAll(stationResponses);
    }

    @DisplayName("출발역과 도착역이 동일한 경우 예외처리")
    @Test
    void firstStationEqualsFinalStationException() {
        assertThrows(FirstStationEqualsFinalStationException.class,
                () -> pathService.findShortestPath(강남역.getId(), 강남역.getId()));
    }

    @DisplayName("존재하지 않는 출발역 또는 도착역인 경우 예외처리")
    @Test
    void stationNotFoundException() {
        long source = 50L;
        long target = 51L;
        assertThrows(NotFoundException.class,
                () -> pathService.findShortestPath(source, target));
    }
}
