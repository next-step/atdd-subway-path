package nextstep.subway.path.domain;

import nextstep.subway.common.exception.BadRequestException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;
import org.assertj.core.api.Assertions;
import org.jgrapht.GraphPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class PathFinderTest {

    Station 교대역;
    Station 강남역;
    Station 남부터미널역;
    Station 양재역;
    Station 부천역;
    Station 역곡역;

    List<Station> stations;
    List<Line> lines;

    /**
     * 교대역   --- *2호선(길이:10)* ---    강남역
     * |                                    |
     * *3호선(길이:2)*                *신분당선(길이:10)*
     * |                                    |
     * 남부터미널역  --- *3호선(길이:3)* --- 양재
     *
     * 부천역 --- *1호선(길이:5)* --- 역곡역
     */
    @BeforeEach
    void setUp() {
        교대역 = new Station("교대역");
        강남역 = new Station("강남역");
        남부터미널역 = new Station("남부터미널역");
        양재역 = new Station("양재역");
        부천역 = new Station("부천역");
        역곡역 = new Station("역곡역");

        stations = Arrays.asList(교대역, 강남역, 남부터미널역, 양재역, 부천역, 역곡역);

        Line 일호선 = Line.builder()
                .name("1호선")
                .color("blue")
                .build();
        일호선.addSection(부천역, 역곡역, 5);

        Line 이호선 = Line.builder()
                .name("2호선")
                .color("green")
                .build();
        이호선.addSection(교대역, 강남역, 10);

        Line 삼호선 = Line.builder()
                .name("3호선")
                .color("orange")
                .build();
        삼호선.addSection(교대역, 남부터미널역, 2);
        삼호선.addSection(남부터미널역, 양재역, 3);

        Line 신분당선 = Line.builder()
                .name("신분당선")
                .color("red")
                .build();
        신분당선.addSection(강남역, 양재역, 10);

        lines = Arrays.asList(일호선, 이호선, 삼호선, 신분당선);
    }

    @Test
    void 출발역부터_도착역까지_최단경로_조회() {
        // given
        PathFinder.PathFinderRequest request = PathFinder.PathFinderRequest.builder()
                .startStation(강남역)
                .endStation(남부터미널역)
                .vertexList(stations)
                .edgeList(lines)
                .build();

        // when
        GraphPath<Station, DefaultWeightedEdge> resultPath = PathFinder.of(request).searchShortestPath();

        // then
        assertThat(resultPath.getVertexList()).containsExactly(강남역, 교대역, 남부터미널역);
    }

    @Test
    void 출발역_도착역_같은경우() {
        // given
        PathFinder.PathFinderRequest request = PathFinder.PathFinderRequest.builder()
                .startStation(강남역)
                .endStation(강남역)
                .vertexList(stations)
                .edgeList(lines)
                .build();

        // when, then
        Assertions.assertThatThrownBy(() ->
                 PathFinder.of(request).searchShortestPath())
                .isInstanceOf(BadRequestException.class)
                .hasMessage(PathFinder.SAME_STATION_MESSAGE);
    }

    @Test
    void 출발역과_도착역이_연결되지않은_경우() {
        // given
        PathFinder.PathFinderRequest request = PathFinder.PathFinderRequest.builder()
                .startStation(부천역)
                .endStation(강남역)
                .vertexList(stations)
                .edgeList(lines)
                .build();

        // when, then
        Assertions.assertThatThrownBy(() ->
                        PathFinder.of(request).searchShortestPath())
                .isInstanceOf(BadRequestException.class)
                .hasMessage(PathFinder.NON_EXIST_PATH_MESSAGE);
    }
}
