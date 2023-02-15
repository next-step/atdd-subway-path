package nextstep.subway.applicaion;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.TestConstructor.AutowireMode;
import org.springframework.transaction.annotation.Transactional;

@DisplayName("구간 관련 기능")
@TestConstructor(autowireMode = AutowireMode.ALL)
@Transactional
@SpringBootTest
class PathFinderServiceTest {

    private final LineRepository lineRepository;
    private final StationRepository stationRepository;
    private final PathService pathService;

    public PathFinderServiceTest(
            final LineRepository lineRepository,
            final StationRepository stationRepository,
            final PathService pathService
    ) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
        this.pathService = pathService;
    }

    private Line 삼호선;
    private Line 이호선;
    private Line 신분당선;
    private Line 수인분당선;

    private Station 남부터미널역;
    private Station 교대역;
    private Station 강남역;
    private Station 양재역;
    private Station 정자역;

    @BeforeEach
    void setUp() {
        lineRepository.deleteAll();
        stationRepository.deleteAll();

        삼호선 = new Line("3호선", "bg-orange-500");
        이호선 = new Line("2호선", "bg-green-500");
        신분당선 = new Line("신분당선", "bg-red-500");
        수인분당선 = new Line("수인분당선", "bg-yellow-500");

        lineRepository.saveAll(List.of(삼호선, 이호선, 신분당선, 수인분당선));

        남부터미널역 = new Station("남부터미널역");
        교대역 = new Station("교대역");
        강남역 = new Station("강남역");
        양재역 = new Station("양재역");
        정자역 = new Station("정자역");

        stationRepository.saveAll(List.of(남부터미널역, 교대역, 강남역, 양재역, 정자역));
    }

    /**
     * 교대역   --- *2호선* ---   강남역
     * |                        |
     * *3호선*                   *신분당선*
     * |                        |
     * 남부터미널역 --- *3호선* --- 양재역  --- *수인분당선* ---  정자역
     */
    @DisplayName("출발역과 도착역을 기준으로 최단 경로를 반환한다.")
    @Test
    void findPath() {
        // given
        삼호선.addSection(new Section(삼호선, 교대역, 남부터미널역, 1));
        삼호선.addSection(new Section(삼호선, 남부터미널역, 양재역, 100));
        이호선.addSection(new Section(이호선, 교대역, 강남역, 4));
        신분당선.addSection(new Section(신분당선, 강남역, 양재역, 5));
        수인분당선.addSection(new Section(수인분당선, 양재역, 정자역, 9));

        // when
        PathResponse path = pathService.findPathBy(남부터미널역.getId(), 정자역.getId());

        // then
        assertAll(
                () -> assertThat(path.getDistance()).isEqualTo(19),
                () -> assertThat(path.getStations()).extracting("name")
                        .containsExactly("남부터미널역", "교대역", "강남역", "양재역", "정자역")
        );
    }
}
