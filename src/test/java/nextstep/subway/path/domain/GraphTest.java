package nextstep.subway.path.domain;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.Section;
import nextstep.subway.path.domain.exception.CannotFindPathException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GraphTest {

    private static final Line LINE1 = new Line("2호선", "green");
    private static final Line LINE2 = new Line("신분당선", "red");
    private Graph graph;

    /**         (LINE 1)        (LINE 2)
     *          1ㅡㅡㅡㅡㅡㅡㅡㅡ    4ㅡㅡ5
     *          |          |
     *          |          |
     *          2 ㅡ ㅡ ㅡ  3
     *
     *         참고: 줄 하나는 길이 1을 의미합니다.
     */
    @BeforeEach
    void setUp() {
        graph = new Graph(List.of(
                new Section(LINE1, 1L, 2L, 2),
                new Section(LINE1, 2L, 3L, 3),
                new Section(LINE1, 1L, 3L, 10),
                new Section(LINE2, 4L, 5L, 2)
        ));
    }

    @DisplayName("최단 경로를 조회한다.")
    @Test
    void findShortestPath() {
        Path path = graph.findShortestPath(1L, 3L);

        assertThat(path.getVertexes()).containsExactly(1L, 2L, 3L);
        assertThat(path.getDistance()).isEqualTo(5);
    }

    @DisplayName("시작점과 종점이 똑같은 경로를 조회하면 예외가 발생한다.")
    @Test
    void findShortestPath_Exception1() {
        assertThatThrownBy(() -> graph.findShortestPath(1L, 1L))
                .isInstanceOf(CannotFindPathException.class)
                .hasMessage("시작점과 종점이 같은 경로를 조회할 수 없습니다.");
    }

    @DisplayName("시작점과 종점이 연결이 되어있지 않을 때 경로를 조회하면 예외가 발생한다.")
    @Test
    void findShortestPath_Exception2() {
        assertThatThrownBy(() -> graph.findShortestPath(1L, 4L))
                .isInstanceOf(CannotFindPathException.class)
                .hasMessage("시작점과 종점이 연결되어 있지 않습니다.");
    }

    @DisplayName("시작점이나 종점이 존재하지 않는 경로를 조회하면 예외가 발생한다.")
    @Test
    void findShortestPath_Exception3() {
        assertThatThrownBy(() -> graph.findShortestPath(9L, 10L))
                .isInstanceOf(CannotFindPathException.class)
                .hasMessage("시작점이나 종점이 존재하지 않습니다.");
    }
}
