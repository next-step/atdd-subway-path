package nextstep.subway.unit.domain;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.PathFinder;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

import static nextstep.subway.unit.PathFinderTestHelper.SHORTEST_DISTANCE_YANGJAE_TO_YEOKSAM;
import static nextstep.subway.unit.PathFinderTestHelper.STATIONS_SIZE_YANGJAE_TO_YEOKSAM;
import static org.assertj.core.api.Assertions.assertThat;

class PathFinderTest {

    private PathFinder pathFinder;
    private Line 신분당선;
    private Line 이호선;
    private Line 삼호선;
    private Station 교대역;
    private Station 강남역;
    private Station 역삼역;
    private Station 남부터미널역;
    private Station 양재역;

    /**
     * 교대역(1) --- *2호선* ---   강남역(2) --- *2호선* ---  역삼역(3)
     * |                            |
     * *3호선*                   *신분당선*
     * |                             |
     * 남부터미널역(4)  --- *3호선* ---   양재역(5)
     */
    @BeforeEach
    void setFixtures() {
        givens();
    }

    @Test
    void 최단_경로_조회() {
        PathResponse response = pathFinder.findShortestPath(양재역, 역삼역);
        assertThat(response.getStations()).hasSize(STATIONS_SIZE_YANGJAE_TO_YEOKSAM);
        assertThat(response.getDistance()).isEqualTo(SHORTEST_DISTANCE_YANGJAE_TO_YEOKSAM);
    }

    void givens() {
        교대역 = new Station("교대역");
        강남역 = new Station("강남역");
        역삼역 = new Station("역삼역");
        남부터미널역 = new Station("남부터미널역");
        양재역 = new Station("양재역");

        ReflectionTestUtils.setField(교대역, "id", 1L);
        ReflectionTestUtils.setField(강남역, "id", 2L);
        ReflectionTestUtils.setField(역삼역, "id", 3L);
        ReflectionTestUtils.setField(남부터미널역, "id", 4L);
        ReflectionTestUtils.setField(양재역, "id", 5L);

        신분당선 = new Line("신분당선", "bg-red-600");
        이호선 = new Line("2호선", "bg-green-600");
        삼호선 = new Line("3호선", "bg-orange-600");

        신분당선.addSection(new Section(신분당선, 강남역, 양재역, 5));
        이호선.addSection(new Section(이호선, 교대역, 강남역, 2));
        이호선.addSection(new Section(이호선, 강남역, 역삼역, 4));
        삼호선.addSection(new Section(삼호선, 교대역, 남부터미널역, 3));
        삼호선.addSection(new Section(삼호선, 남부터미널역, 양재역, 3));

        List<Line> lines = new ArrayList<>();
        lines.add(신분당선);
        lines.add(이호선);
        lines.add(삼호선);

        pathFinder = PathFinder.of(lines);
    }
}
