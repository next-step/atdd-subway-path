package nextstep.subway.path.domain;

import nextstep.subway.path.domain.exception.CannotFindPathException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GraphTest {

    private List<Long> vertexes;
    private List<Edge> edges;
    private Graph pathFinder;

    /**
     *          1---------     4
     *          |        |
     *          | / ㅡ ㅡ 3
     *          2
     */
    @BeforeEach
    void setUp() {
        vertexes = List.of(1L, 2L, 3L, 4L);
        edges = List.of(
                new Edge(1L, 2L, 2),
                new Edge(2L, 3L, 3),
                new Edge(1L, 3L, 10));
        pathFinder = new Graph(vertexes, edges);
    }

    @DisplayName("최단 경로를 조회한다.")
    @Test
    void findShortestPath() {
        Path path = pathFinder.findShortestPath(1L, 3L);

        assertThat(path.getVertexes()).containsExactly(1L, 2L, 3L);
        assertThat(path.getDistance()).isEqualTo(5);
    }

    @DisplayName("시작점과 종점이 똑같은 경로를 조회하면 예외가 발생한다.")
    @Test
    void findShortestPath_Exception1() {
        assertThatThrownBy(() -> pathFinder.findShortestPath(1L, 1L))
                .isInstanceOf(CannotFindPathException.class)
                .hasMessage("시작점과 종점이 같은 경로를 조회할 수 없습니다.");
    }

    @DisplayName("시작점과 종점이 연결이 되어있지 않을 때 경로를 조회하면 예외가 발생한다.")
    @Test
    void findShortestPath_Exception2() {
        assertThatThrownBy(() -> pathFinder.findShortestPath(1L, 4L))
                .isInstanceOf(CannotFindPathException.class)
                .hasMessage("시작점과 종점이 연결되어 있지 않습니다.");
    }

    @DisplayName("시작점이나 종점이 존재하지 않는 경로를 조회하면 예외가 발생한다.")
    @Test
    void findShortestPath_Exception3() {
        assertThatThrownBy(() -> pathFinder.findShortestPath(1L, 5L))
                .isInstanceOf(CannotFindPathException.class)
                .hasMessage("시작점이나 종점이 존재하지 않습니다.");
    }
}
