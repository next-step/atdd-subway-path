package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.PathFinder;
import nextstep.subway.domain.Station;
import nextstep.subway.exception.BadRequestException;
import org.jgrapht.GraphPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static nextstep.subway.unit.LineTestUtil.개봉역;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertAll;

class SectionPathTest {

    static Station 교대역 = new Station(3L, "교대역");
    static Station 강남역 = new Station(4L, "강남역");
    static Station 양재역 = new Station(5L, "양재역");
    static Station 남부터미널역 = new Station(6L, "남부터미널역");

    Line 이호선 = new Line(2L, "이호선", "red"  );
    Line 삼호선 = new Line(3L, "삼호선", "yellow"  );
    Line 신분당선 = new Line(4L, "신분당선", "white"  );

    GraphPath<Station, DefaultWeightedEdge> shortestPath;

    @BeforeEach
    void setUp(){
        이호선.addSection(1L, 교대역, 강남역, 10);
        신분당선.addSection(2L,강남역, 양재역,10);
        삼호선.addSection(3L, 교대역, 남부터미널역, 2);
        삼호선.addSection(4L, 남부터미널역, 양재역, 3);
    }

    /**
     * 교대역    --- *2호선* ---   강남역
     * |                        |
     * *3호선*                   *신분당선*
     * |                        |
     * 남부터미널역  --- *3호선* ---   양재
     */

    @Test
    @DisplayName("구간 최단거리 조회.")
    void getShortestPath() {


        List<Line>  lines = List.of(이호선, 신분당선, 삼호선);
        PathFinder pathFinder = new PathFinder(lines);

        shortestPath = pathFinder.getShortestPath(교대역, 양재역);

        assertAll(
                () -> assertThat(shortestPath.getVertexList()).containsExactly(교대역, 남부터미널역, 양재역),
                () -> assertThat(shortestPath.getWeight()).isEqualTo(5)
        );

    }

    /**
     * When 출발역과 도착역을 똑같이 요청하면
     * Then 거리 조회에 실패한다.
     */
    @Test
    @DisplayName("구간 최단거리 조회를 실패한다.")
    void getShortestPath_fail1() {

        List<Line>  lines = List.of(이호선, 신분당선, 삼호선);
        PathFinder pathFinder = new PathFinder(lines);

        assertThatExceptionOfType(BadRequestException.class)
                .isThrownBy(() -> {
                    pathFinder.getShortestPath(교대역, 교대역);
                });

    }

    /**
     * When 존재하지 않는 출발역이나 도착역을 조회 할 경우
     * Then 거리 조회에 실패한다.
     */
    @Test
    @DisplayName("구간 최단거리 조회를 실패한다.")
    void getShortestPath_fail2() {

        List<Line>  lines = List.of(이호선, 신분당선, 삼호선);
        PathFinder pathFinder = new PathFinder(lines);


        assertThatExceptionOfType(BadRequestException.class)
                .isThrownBy(() -> {
                    pathFinder.getShortestPath(교대역, 개봉역);
                });

    }

}
