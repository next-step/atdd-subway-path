package nextstep.subway.unit;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.PathStationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.PathFinder;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class PathFinderTest {
    List<Line> lines = new ArrayList<>();
    Station 강남역;
    Station 교대역;
    Station 삼성역;
    Line 이호선;
    Line 신분당선;
    Line 삼호선;
    PathFinder pathFinder;


    @BeforeEach
    void setUp() {
        pathFinder = new PathFinder();

        강남역 = new Station("강남역");
        교대역 = new Station("교대역");
        삼성역 = new Station("삼성역");

        이호선 = new Line("2호선", "green");
        이호선.getSections().add(new Section(이호선, 강남역, 교대역, 5));

        신분당선 = new Line("신분당선", "red");
        신분당선.getSections().add(new Section(신분당선, 교대역, 삼성역, 2));

        삼호선 = new Line("3호선", "orange");
        삼호선.getSections().add(new Section(삼호선, 강남역, 삼성역, 12));

        lines.addAll(Arrays.asList(이호선, 신분당선, 삼호선));
    }

    @Test
    @DisplayName("최단 경로 찾기 - Happy Case")
    void shortestPath() {
        //when
        PathResponse pathResponse = pathFinder.shortestPath(lines, 강남역, 삼성역);

        //then
        List<PathStationResponse> stations = pathResponse.getStations();
        List<String> names = stations.stream().map(PathStationResponse::getName).collect(toList());

        assertThat(pathResponse.getDistance()).isEqualTo(7);
        assertThat(stations).hasSize(3);
        assertThat(names).containsExactly("강남역", "교대역", "삼성역");
    }

    @Test
    @DisplayName("(예외) 출발지와 도착지가 같은 경우 - 최단 경로 찾기")
    void shortestPath_exception_case1() {
        //when then
        assertThrows(IllegalArgumentException.class, () -> pathFinder.shortestPath(lines, 강남역, 강남역));
    }

    @Test
    @DisplayName("(예외) 출발지와 도착지가 연결되지 않은 경우 - 최단 경로 찾기")
    void shortestPath_exception_case4() {
        //given
        Station 연신내역 = new Station("연신내역");
        Station 불광역 = new Station("불광역");
        Line 칠호선 = new Line("7호선", "blue");
        칠호선.getSections().add(new Section(칠호선, 연신내역, 불광역, 10));
        lines.add(칠호선);

        //when then
        assertThrows(IllegalArgumentException.class, () -> pathFinder.shortestPath(lines, 강남역, 연신내역));
    }
}