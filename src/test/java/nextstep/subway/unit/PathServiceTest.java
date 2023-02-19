package nextstep.subway.unit;


import nextstep.subway.applicaion.PathFinderService;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.dto.PathResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@Transactional
@DisplayName("지하철 경로 조회 서비스 단위 테스트")
public class PathServiceTest {

    @Autowired
    private PathFinderService pathFinderService;
    @Autowired
    private LineRepository lineRepository;

    private Station 교대역;
    private Station 강남역;
    private Station 양재역;
    private Station 남부터미널역;

    private Line 이호선;
    private Line 신분당선;
    private Line 삼호선;

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

        이호선 = new Line("2호선", "green", new Section(교대역, 강남역, 10));
        신분당선 = new Line("신분당선", "red", new Section(강남역, 양재역, 10));
        삼호선 = new Line("3호선", "orange", new Section(교대역, 남부터미널역, 2));

        삼호선.addSection(new Section(삼호선, 남부터미널역, 양재역, 3));

        lineRepository.save(이호선);
        lineRepository.save(신분당선);
        lineRepository.save(삼호선);
    }

    @Test
    @DisplayName("경로 조회")
    void findPath() {
        // when
        PathResponse path = pathFinderService.findPath(교대역.getId(), 양재역.getId());

        // then
        assertAll(
                () -> assertThat(path.getDistance()).isEqualTo(5),
                () -> assertThat(path.getStations()).containsExactly(교대역, 남부터미널역, 양재역)
        );
    }
}
