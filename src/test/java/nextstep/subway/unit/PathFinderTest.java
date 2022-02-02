package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import nextstep.subway.applicaion.PathFinder;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import org.jgrapht.GraphPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

class PathFinderTest {

    private PathFinder pathFinder = new PathFinder();

    private static final Station 강남역 = new Station("강남역");
    private static final Station 역삼역 = new Station("역삼역");
    private static final Station 천호역 = new Station("천호역");
    private static final Station 강동구청역 = new Station("강동구청역");

    /*
      강남역(1)    --- *2호선(10)* ---   역삼역(2)
      |
      *3호선(34)
      |
      천호역(3)     -- *8호선(20)*---   강동구청역(4)
 */

    @Test
    @DisplayName("최단거리 조회")
    void searchShortestPath() {
        //given
        Line 이호선 = new Line("2호선", "초록색");
        이호선.registerSection(강남역, 역삼역, 10);

        Line 삼호선 = new Line("3호선", "초록색");
        삼호선.registerSection(강남역, 천호역, 34);

        Line 팔호선 = new Line("3호선", "초록색");
        팔호선.registerSection(천호역, 강동구청역, 20);

        //when
        GraphPath<Station, DefaultWeightedEdge> response = pathFinder.getPath(Arrays.asList(이호선, 삼호선, 팔호선), 역삼역, 강동구청역);

        //then
        assertThat(response.getVertexList()).containsExactly(역삼역, 강남역, 천호역, 강동구청역);
        assertThat(response.getWeight()).isEqualTo(64);
    }

    @Test
    @DisplayName("최단거리 조회시 출발역과 도착역을 잇는 노선이 없으면 조회가 불가능하다.")
    void searchShortestPath2() {
        //given
        Line 이호선 = new Line("2호선", "초록색");
        이호선.registerSection(강남역, 역삼역, 10);

        Line 삼호선 = new Line("3호선", "초록색");
        삼호선.registerSection(강남역, 천호역, 34);

        //when, then
        assertThrows(IllegalArgumentException.class, () -> pathFinder.getPath(Arrays.asList(이호선, 삼호선), 역삼역, 강동구청역));
    }
}