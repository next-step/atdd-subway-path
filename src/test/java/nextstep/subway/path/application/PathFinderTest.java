package nextstep.subway.path.application;

import nextstep.subway.line.application.LineService;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.Section;
import nextstep.subway.path.PathFinder;
import nextstep.subway.path.PathResponse;
import nextstep.subway.path.domain.PathResult;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("경로 탐색 관련 테스트")
@SpringBootTest
@Transactional
public class PathFinderTest {

    @Autowired
    private PathFinder pathFinder;

    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private LineRepository lineRepository;
    @Autowired
    private LineService lineService;

    Station 강남역;
    Station 양재역;
    Station 교대역;
    Station 남부터미널역;
    Line 신분당선;
    Line 이호선;
    Line 삼호선;

    @BeforeEach
    void setUp() {
        강남역 = stationRepository.save(new Station("강남역"));
        양재역 = stationRepository.save(new Station("양재역"));
        교대역 = stationRepository.save(new Station("교대역"));
        남부터미널역 = stationRepository.save(new Station("남부터미널역"));

        신분당선 = lineRepository.save(new Line("신분당선", "bg-red-600", 강남역, 양재역, 10));
        이호선 = lineRepository.save(new Line("이호선", "bg-red-600", 양재역, 남부터미널역, 10));
        삼호선 = lineRepository.save(new Line("삼호선", "bg-red-600", 강남역, 교대역, 5));
        삼호선.addSection(교대역, 남부터미널역, 3);
    }

    @Test
    void findPath() {
        // given
        List<Section> sections = lineService.getSections();

        // when
        PathResult path = pathFinder.findPath(sections, 강남역, 남부터미널역);

        // then
        assertThat(path.getStations().size()).isEqualTo(3);
        assertThat(path.getStations()).containsExactly(강남역, 교대역, 남부터미널역);
        assertThat(path.getDistance()).isEqualTo(8);
    }
}
