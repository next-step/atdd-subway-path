package nextstep.subway.unit;

import nextstep.subway.domain.Graph;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.exception.paths.CannotFindPathException;
import nextstep.subway.exception.paths.EmptyLineException;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Graph 도메인 단위 테스트")
public class GraphTest {

    static Station 교대역;
    static Station 강남역;
    static Station 양재역;
    static Station 남부터미널역;
    static Station 없는역;

    static Line 이호선;
    static Line 삼호선;
    static Line 신분당선;

    @BeforeEach
    void setUp() {
        교대역 = new Station(1L, "교대역");
        강남역 = new Station(2L, "강남역");
        양재역 = new Station(3L, "양재역");
        남부터미널역 = new Station(4L, "남부터미널역");

        이호선 = new Line(5L, "2호선", "green");
        삼호선 = new Line(6L, "3호선", "yellow");
        신분당선 = new Line(7L, "신분당선", "red");

        이호선.addSection(new Section(8L, 이호선, 교대역, 강남역, 10));
        신분당선.addSection(new Section(9L, 신분당선, 강남역, 양재역, 3));
        삼호선.addSection(new Section(10L, 삼호선, 교대역, 남부터미널역, 5));
        삼호선.addSection(new Section(11L, 삼호선, 남부터미널역, 양재역, 5));
    }

    @DisplayName("생성자로 넘겨받은 line이 비어있을 경우 Graph를 생성할 수 없다")
    @Test
    public void create_graph_fail_by_empty_line() {
        ThrowableAssert.ThrowingCallable actual = () -> new Graph(Collections.emptyList());

        assertThatThrownBy(actual)
                .isInstanceOf(EmptyLineException.class)
                .hasMessage("노선이 없으면 그레프를 만들 수 없습니다");
    }

    @DisplayName("시작역이나 도착역이 없는 경우 예외가 발생한다")
    @ParameterizedTest
    @MethodSource("param")
    public void not_exists_start_or_arrival_station(Station 출발역, Station 도착역, String expectedMessage) {
        // given
        Graph graph = new Graph(List.of(이호선, 삼호선, 신분당선));

        // when
        ThrowableAssert.ThrowingCallable actual = () -> graph.getShortestPath(출발역, 도착역);

        // then
        assertThatThrownBy(actual)
                .isInstanceOf(CannotFindPathException.class)
                .hasMessage(expectedMessage);
    }

    @DisplayName("시작역이나 도착역이 없는 경우 예외가 발생한다")
    @ParameterizedTest
    @MethodSource("param")
    public void get_distance_not_exists_start_or_arrival_station(Station 출발역, Station 도착역, String expectedMessage) {
        // given
        Graph graph = new Graph(List.of(이호선, 삼호선, 신분당선));

        // when
        ThrowableAssert.ThrowingCallable actual = () -> graph.getShortestPath(출발역, 도착역);

        // then
        assertThatThrownBy(actual)
                .isInstanceOf(CannotFindPathException.class)
                .hasMessage(expectedMessage);
    }

    private static Stream<Arguments> param() {
        return Stream.of(
                Arguments.of(강남역, 없는역, "출발역, 도착역 중 하나라도 없으면 최단경로를 찾을 수 없습니다"),
                Arguments.of(없는역, 강남역, "출발역, 도착역 중 하나라도 없으면 최단경로를 찾을 수 없습니다")
        );
    }

    @DisplayName("Graph 도메인을 통해 최단 경로를 찾아온다")
    @Test
    public void find_shortest_path() {
        // when
        Graph graph = new Graph(List.of(이호선, 삼호선, 신분당선));

        // then
        assertAll(
                () -> assertThat(graph.getShortestPath(강남역, 남부터미널역)).containsExactly(강남역, 양재역, 남부터미널역),
                () -> assertThat(graph.getShortestDistance(강남역, 남부터미널역)).isEqualTo(8)
        );
    }
}
