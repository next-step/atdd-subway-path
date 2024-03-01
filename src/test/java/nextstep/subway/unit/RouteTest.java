package nextstep.subway.unit;

import nextstep.subway.line.Line;
import nextstep.subway.line.Section;
import nextstep.subway.path.Route;
import nextstep.subway.station.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class RouteTest {

    /**
     * 교대역    -- 4 -- *2호선* ---   강남역
     * |                                    |
     * *3호선*                         *신분당선*
     * *2*                                 *5*
     * |                                    |
     * 남부터미널역  -- 3 -- *3호선* ---   양재
     */
    private Station 교대역;
    private Station 강남역;
    private Station 양재역;
    private Station 남부터미널역;

    private Line 이호선;
    private Line 신분당선;
    private Line 삼호선;

    private List<Section> sections;

    @BeforeEach
    void setUp() {
        교대역 = new Station(1L, "교대역");
        강남역 = new Station(2L, "강남역");
        양재역 = new Station(3L, "양재역");
        남부터미널역 = new Station(4L, "남부터미널역");

        이호선 = new Line("2호선", "green", 교대역, 강남역, 4L);
        신분당선 = new Line("신분당선", "red", 강남역, 양재역, 5L);
        삼호선 = new Line("3호선", "orange", 교대역, 남부터미널역, 2L);

        삼호선.addNewSection(남부터미널역, 양재역, 3L);

        sections = List.of(
            new Section(이호선, 교대역, 강남역, 4L),
            new Section(신분당선, 강남역, 양재역, 5L),
            new Section(삼호선, 교대역, 남부터미널역, 2L),
            new Section(삼호선, 남부터미널역, 양재역, 3L)
        );
    }

    @DisplayName("경로 조회 기능")
    @Test
    void findPath() {
        // given
        Route route = new Route();
        route.initGraph(sections);

        // when
        List<Station> stations = route.findShortestPath(교대역, 양재역);

        // then
        assertThat(stations).containsExactly(교대역, 남부터미널역, 양재역);
    }

    @DisplayName("최단 거리 조회 기능")
    @Test
    void findShortestDistance() {
        // given
        Route route = new Route();
        route.initGraph(sections);

        // when
        int distance = route.findShortestDistance(교대역, 양재역);

        // then
        assertThat(distance).isEqualTo(5);
    }
}
