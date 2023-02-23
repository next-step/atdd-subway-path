package nextstep.subway.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class PathTest {

    private Station 교대역;
    private Station 강남역;
    private Station 양재역;
    private Station 남부터미널역;
    private Line 이호선;
    private Line 신분당선;
    private Line 삼호선;
    private Path path;

    @BeforeEach
    void setUp() {
        교대역 = new Station("교대역");
        강남역 = new Station("강남역");
        양재역 = new Station("양재역");
        남부터미널역 = new Station("남부터미널역");

        이호선 = new Line("2호선", "green");
        이호선.addSection(교대역, 강남역, 10);
        신분당선 = new Line("신분당선", "red");
        신분당선.addSection(강남역, 양재역, 10);
        삼호선 = new Line("3호선", "orange");
        삼호선.addSection(교대역, 남부터미널역, 2);
        삼호선.addSection(남부터미널역, 양재역, 3);
        path = new Path(List.of(이호선, 신분당선, 삼호선));
    }

    @Test
    void findShortestPath() {
        // when
        final List<Station> stations = path.findShortestPath(교대역, 양재역);

        // then
        assertThat(stations).containsExactly(교대역, 남부터미널역, 양재역);
    }

    @Test
    void findShortestPathDistance() {
        // when
        final int distance = path.findShortestPathDistance(교대역, 양재역);

        // then
        assertThat(distance).isEqualTo(5);
    }
}
