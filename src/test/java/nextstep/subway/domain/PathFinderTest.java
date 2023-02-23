package nextstep.subway.domain;

import nextstep.subway.error.ErrorCode;
import nextstep.subway.error.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class PathFinderTest {

    private Station 교대역;
    private Station 강남역;
    private Station 양재역;
    private Station 남부터미널역;
    private Line 이호선;
    private Line 신분당선;
    private Line 삼호선;
    private PathFinder pathFinder;

    @BeforeEach
    void setUp() {
        교대역 = new Station("교대역");
        강남역 = new Station("강남역");
        양재역 = new Station("양재역");
        남부터미널역 = new Station("남부터미널역");

        이호선 = new Line("2호선", "green");
        이호선.addSection(교대역, 강남역, 10);
        신분당선 = new Line("신분당선", "red");
        신분당선.addSection(강남역, 양재역, 10);
        삼호선 = new Line("3호선", "orange");
        삼호선.addSection(교대역, 남부터미널역, 2);
        삼호선.addSection(남부터미널역, 양재역, 3);
        pathFinder = new PathFinder(List.of(이호선, 신분당선, 삼호선));
    }

    @DisplayName("최단 경로의 역들 조회")
    @Test
    void findShortestPath() {
        // when
        final List<Station> stations = pathFinder.findShortestPath(교대역, 양재역);

        // then
        assertThat(stations).containsExactly(교대역, 남부터미널역, 양재역);
    }

    @DisplayName("")
    @Test
    void findShortestPathDistance() {
        // when
        final int distance = pathFinder.findShortestPathDistance(교대역, 양재역);

        // then
        assertThat(distance).isEqualTo(5);
    }

    @DisplayName("출발역과 도착역이 같을 경우 에러 발생")
    @Test
    void cannotFindShortestPathWhenSourceAndTargetIsSame() {
        // when, then
        assertThatThrownBy(() -> {
            pathFinder.findShortestPath(교대역, 교대역);
        }).isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.SOURCE_AND_TARGET_STATION_IS_SAME.getMessage());
    }

    @DisplayName("출발역과 도착역이 연결되어있지 않을 경우 에러 발생j")
    @Test
    void cannotFindShortestPathWhenSourceAndTargetIsNotConnected() {
        // given
        삼호선.removeSection(교대역);
        pathFinder = new PathFinder(List.of(이호선, 삼호선));

        // when, then
        assertThatThrownBy(() -> {
            pathFinder.findShortestPath(교대역, 양재역);
        }).isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.SOURCE_AND_TARGET_STATION_IS_NOT_CONNECTED.getMessage());
    }

    @DisplayName("존재하지 않은 출발역이나 도착역을 조회할 경우 에러 발생")
    @Test
    void cannotFindShortestPathWhenSourceAndTargetIsNotExists() {
        // given
        final Station 선유도역 = new Station("선유도역");

        // when, then
        assertThatThrownBy(() -> {
            pathFinder.findShortestPath(선유도역, 양재역);
        }).isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.STATION_NOT_FOUND.getMessage());
    }
}
