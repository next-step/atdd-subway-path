package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.PathFinder;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static nextstep.subway.fixture.ConstStation.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class PathFinderTest {

    private static final String NEW_BUN_DANG = "신분당선";
    private static final String BG_RED_600 = "bg-red-600";
    private static final String BUN_DANG = "분당선";
    private static final String BG_YELLOW_600 = "bg-yellow-600";

    private Line 신분당선;
    private Line 분당선;

    @BeforeEach
    void setUp() {
        신분당선 = Line.of(NEW_BUN_DANG, BG_RED_600);
        분당선 = Line.of(BUN_DANG, BG_YELLOW_600);

        신분당선.addSection(Section.of(신논현역, 강남역, 10));
        신분당선.addSection(Section.of(강남역, 판교역, 15));
        분당선.addSection(Section.of(판교역, 정자역, 20));
        분당선.addSection(Section.of(정자역, 이매역, 25));
    }

    @DisplayName("지하철 경로 조회")
    @Test
    void findPathSourceToTarget() {
        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        DijkstraShortestPath<Station, DefaultWeightedEdge> pathAlgorithm = new DijkstraShortestPath<>(graph);

        PathFinder pathFinder = PathFinder.of(graph, pathAlgorithm, Arrays.asList(신분당선, 분당선));

        List<Station> path = pathFinder.findPath(신논현역, 이매역);
        double pathWeight = pathFinder.findPathWeight(신논현역, 이매역);

        assertAll(
                () -> assertThat(path).hasSize(5),
                () -> assertThat(path).extracting("name").containsExactly("신논현역", "강남역", "판교역", "정자역", "이매역"),
                () -> assertThat(pathWeight).isEqualTo(70)
        );
    }
}
