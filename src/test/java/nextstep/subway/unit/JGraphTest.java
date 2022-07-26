package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.Arrays;
import java.util.List;
import nextstep.subway.domain.JGraph;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class JGraphTest {
    private Station 교대역;
    private Station 강남역;
    private Station 양재역;
    private Station 남부터미널역;

    private Line 이호선;
    private Line 신분당선;
    private Line 삼호선;

    /**
     * 교대역    --- *2호선(10)* ---   강남역
     * |                                |
     * *3호선(2)*                   *신분당선(10)*
     * |                                |
     * 남부터미널역  --- *3호선(3)* ---   양재
     */
    @BeforeEach
    public void setUp() {
        교대역 = new Station("교대역");
        강남역 = new Station("강남역");
        양재역 = new Station("양재역");
        남부터미널역 = new Station("남부터미널역");

        이호선 = new Line("2호선", "green");
        신분당선 = new Line("신분당선", "red");
        삼호선 = new Line("삼호선", "oragne");

        이호선.addSection(교대역, 강남역, 10);
        신분당선.addSection(강남역, 양재역, 10);
        삼호선.addSection(교대역, 남부터미널역, 2);
        삼호선.addSection(남부터미널역, 양재역, 3);
    }


    /**
     * 교대역    --- *2호선(10)* ---        강남역(출발역)
     * |                                    |
     * *3호선(2)*                         *신분당선(10)*
     * |                                    |
     * 남부터미널역(도착역)  --- *3호선(3)* ---   양재
     */
    @DisplayName("환승을 포함한 최단거리 경로 조회 성공")
    @Test
    void transferShortestDistance(){
        // given
        JGraph graph = new JGraph(Arrays.asList(이호선, 삼호선, 신분당선));

        // when
        List<Station> shortestPath = graph.findShortestPath(강남역, 남부터미널역);
        int shortestDistance = graph.getShortestDistance(강남역, 남부터미널역);

        // then
        assertAll(
                () -> assertThat(shortestPath).containsExactly(강남역, 교대역, 남부터미널역),
                () -> assertThat(shortestDistance).isEqualTo(12L)
        );
    }

    /**
     * 교대역(출발역)    --- *2호선(10)* ---     강남역
     * |                                    |
     * *3호선(2)*                         *신분당선(10)*
     * |                                    |
     * 남부터미널역    --- *3호선(3)* ---       양재역(도착역)
     */
    @DisplayName("환승을 포함하지 않은 최단거리 경로 조회 성공")
    @Test
    void doseNotTransferShortestDistance(){
        // given
        JGraph graph = new JGraph(Arrays.asList(이호선, 삼호선, 신분당선));

        // when
        List<Station> shortestPath = graph.findShortestPath(교대역, 양재역);
        int shortestDistance = graph.getShortestDistance(교대역, 양재역);

        // then
        assertAll(
                () -> assertThat(shortestPath).containsExactly(교대역, 남부터미널역, 양재역),
                () -> assertThat(shortestDistance).isEqualTo(5L)
        );
    }
}
