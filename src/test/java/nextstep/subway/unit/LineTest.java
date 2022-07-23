package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련")
class LineTest {

    @DisplayName("지하철 구간 추가")
    @Test
    void addSection() {
        // given
        Line line = new Line("2호선", "green");

        // when
        line.addSection(Stub.구로디지털단지역, Stub.신대방역, 10);

        // then
        assertThat(line.getStations()).containsExactly(Stub.구로디지털단지역, Stub.신대방역);
    }

    @DisplayName("지하철 노선에 등록된 지하철역 조회")
    @Test
    void getStations() {
        // given
        Line line = Stub.이호선_생성.get();

        // when
        List<Station> stations = line.getStations();

        // then
        assertThat(stations).contains(Stub.구로디지털단지역, Stub.신대방역, Stub.신림역);
    }

    @DisplayName("지하철 구간 삭제")
    @Test
    void removeSection() {
        // given
        Line line = Stub.이호선_생성.get();

        // when
        line.removeSection(Stub.신림역);

        // then
        assertThat(line.getStations()).contains(Stub.구로디지털단지역, Stub.신대방역);
    }

    @DisplayName("지하철 노선 수정")
    @Test
    void updateLine() {
        // given
        Line line = Stub.이호선_생성.get();

        // when
        line.update("신분당선", "red");

        // then
        assertThat(line.getName()).isEqualTo("신분당선");
        assertThat(line.getColor()).isEqualTo("red");
    }

    @DisplayName("지하철 노선을 경로 찾기 그래프로 변환")
    @Test
    void toGraph() {
        // given
        Line line = Stub.이호선_생성.get();

        // when
        WeightedMultigraph<Station, DefaultWeightedEdge> graph = line.toGraph();

        // then
        DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(graph);
        List<Station> paths = dijkstraShortestPath.getPath(Stub.구로디지털단지역, Stub.신림역).getVertexList();
        assertThat(paths).containsExactly(Stub.구로디지털단지역, Stub.신대방역, Stub.신림역);
    }

    private static class Stub {
        public static final Station 구로디지털단지역 = new Station("구로디지털단지역");
        public static final Station 신대방역 = new Station("신대방역");
        public static final Station 신림역 = new Station("신림역");
        public static final Supplier<Line> 이호선_생성 = () -> {
            Line line = new Line("2호선", "green");
            line.addSection(구로디지털단지역, 신대방역, 10);
            line.addSection(신대방역, 신림역, 8);
            return line;
        };
    }
}
