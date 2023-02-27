package nextstep.subway.unit;

import nextstep.subway.applicaion.PathService;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@Transactional
public class PathServiceTest {
    @Autowired
    private PathService pathService;
    @Autowired
    private LineRepository lineRepository;

    private Station 교대역;
    private Station 강남역;
    private Station 양재역;
    private Station 남부터미널역;
    private Line 이호선;
    private Line 신분당선;
    private Line 삼호선;
    private List<Line> 전체_노선_목록;

    /**
     * 교대역    --- *2호선* ---   강남역
     * |                        |
     * *3호선*                   *신분당선*
     * |                        |
     * 남부터미널역  --- *3호선* ---   양재
     */
    @BeforeEach
    void setUp() {
        // given
        교대역 = new Station("교대역");
        강남역 = new Station("강남역");
        양재역 = new Station("양재역");
        남부터미널역 = new Station("남부터미널역");

        이호선 = new Line("2호선", "green", 교대역, 강남역, 10);
        신분당선 = new Line("신분당선", "red", 강남역, 양재역, 10);
        삼호선 = new Line("3호선", "orange", 교대역, 남부터미널역, 2);
        삼호선.addSection(남부터미널역, 양재역, 3);

        lineRepository.save(이호선);
        lineRepository.save(신분당선);
        lineRepository.save(삼호선);

        전체_노선_목록 = List.of(이호선, 신분당선, 삼호선);
    }

    @Test
    @DisplayName("경로 조회")
    void findPath() {
        // when
        PathResponse path = pathService.findPath(강남역.getId(), 남부터미널역.getId());

        // then
        assertAll(
                () -> assertThat(path.getDistance()).isEqualTo(12),
                () -> assertThat(path.getStations()).extracting(StationResponse::getName)
                        .containsExactly("강남역", "교대역", "남부터미널역")
        );
    }
}
