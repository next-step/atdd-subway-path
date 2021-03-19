package nextstep.subway.path.domain;

import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;
import org.jgrapht.GraphPath;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("경로 Domain 단위 테스트")
public class PathFinderTest {

    private Station 교대역 = new Station("교대역");
    private Station 강남역 = new Station("강남역");
    private Station 양재역 = new Station("양재역");
    private Station 남부터미널역 = new Station("남부터미널역");

    private Line 신분당선 = new Line("신분당선", "red", 강남역, 양재역, 5);
    private Line 이호선 = new Line("이호선", "green", 교대역, 강남역, 5);
    private Line 삼호선 = new Line("삼호선", "orange", 교대역, 남부터미널역, 5);

    @DisplayName("지하철 최단 경로 조회")
    @Test
    void findShortestPathStation() {
        List<Line> lines = new ArrayList<>();
        lines.add(이호선);
        lines.add(삼호선);
        lines.add(신분당선);

        PathFinder path = new PathFinder(lines);

        GraphPath shortestPath = path.getShortestPath(강남역, 남부터미널역);

        assertThat(shortestPath.getVertexList()).hasSize(3);
        assertThat((int) shortestPath.getWeight()).isEqualTo(10);
    }
}
