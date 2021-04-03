package nextstep.subway.path.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.path.PathResponse;
import nextstep.subway.path.PathService;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.utils.DatabaseCleanup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;


@DisplayName("경로 탐색 서비스 테스트 관련")
@SpringBootTest
@Transactional
public class PathServiceTest {

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private PathService pathService;

    @Autowired
    private DatabaseCleanup databaseCleanup;

    private Station 강남역;
    private Station 양재역;
    private Station 교대역;
    private Station 남부터미널역;
    private Line 신분당선;
    private Line 이호선;
    private Line 삼호선;

    @BeforeEach
    void setUp() {
        databaseCleanup.execute();
        강남역 = stationRepository.save(new Station("강남역"));
        양재역 = stationRepository.save(new Station("양재역"));
        교대역 = stationRepository.save(new Station("교대역"));
        남부터미널역 = stationRepository.save(new Station("남부터미널역"));

        신분당선 = lineRepository.save(new Line("신분당선", "bg-red-600", 강남역, 양재역, 10));
        이호선 = lineRepository.save(new Line("이호선", "bg-red-600", 양재역, 남부터미널역, 10));
        삼호선 = lineRepository.save(new Line("삼호선", "bg-red-600", 강남역, 교대역, 5));
        삼호선.addSection(교대역, 남부터미널역, 3);
    }

    @DisplayName("최단 경로를 조회한다")
    @Test
    void findShortestPath() {
        // given
        Long source = 1L;
        Long target = 4L;

        // when
        PathResponse response = pathService.findShortestPath(source, target);

        // then
        assertThat(response.getStations().size()).isEqualTo(3);
    }
}
