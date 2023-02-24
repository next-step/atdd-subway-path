package nextstep.subway.unit;

import nextstep.subway.applicaion.PathFinder;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import nextstep.subway.exception.EntityNotFoundException;
import nextstep.subway.infra.DijkstraShortestPathFinder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class PathFinderTest {
    private PathFinder pathFinder;

    private Station 교대역;
    private Station 강남역;
    private Station 양재역;
    private Station 남부터미널역;
    private Station 죽전역;
    private Station 보정역;

    private Line 이호선;
    private Line 신분당선;
    private Line 삼호선;
    private Line 수인분당선;

    private List<Line> 전체_노선_목록;

    /**
     * 교대역    --- *2호선* ---   강남역
     * |                        |
     * *3호선*                   *신분당선*
     * |                        |
     * 남부터미널역  --- *3호선* --- 양재역
     */
    @BeforeEach
    void setUp() {
        // given
        pathFinder = new DijkstraShortestPathFinder();

        교대역 = new Station("교대역");
        강남역 = new Station("강남역");
        양재역 = new Station("양재역");
        남부터미널역 = new Station("남부터미널역");
        죽전역 = new Station("죽전역");
        보정역 = new Station("보정역");

        이호선 = new Line("2호선", "green", 교대역, 강남역, 10);
        신분당선 = new Line("신분당선", "red", 강남역, 양재역, 10);
        삼호선 = new Line("3호선", "orange", 교대역, 남부터미널역, 2);
        수인분당선 = new Line("수인분당선", "yellow", 죽전역, 보정역, 2);

        삼호선.addSection(남부터미널역, 양재역, 3);

        전체_노선_목록 = List.of(이호선, 신분당선, 삼호선);
    }

    @Test
    @DisplayName("출발역과 도착역을 같게 조회")
    void findPath_WithSameStation() {
        // when
        // then
        assertThatThrownBy(() -> pathFinder.find(전체_노선_목록, 강남역, 강남역))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("The departure and arrival stations must not be the same.");

    }

    @Test
    @DisplayName("등록되어 있지 않은 역을 조회")
    void findPath_WithUnregisteredStation() {
        // when
        // then
        assertThatThrownBy(() -> pathFinder.find(전체_노선_목록, 죽전역, 강남역))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("This station does not exist.");
    }

    @Test
    @DisplayName("연결되어 있지 않은 역을 조회")
    void findPath_WithNotConnectedStation() {
        // given
        전체_노선_목록 = List.of(이호선, 신분당선, 삼호선, 수인분당선);

        // when
        // then
        assertThatThrownBy(() -> pathFinder.find(전체_노선_목록, 죽전역, 강남역))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Unconnected station.");
    }

    @Test
    @DisplayName("최단거리 경로로 조회")
    void findPath() {
        // when
        PathResponse response = pathFinder.find(전체_노선_목록, 교대역, 양재역);

        // then
        assertAll(() -> {
                    assertThat(response.getStations()).containsExactly(교대역, 남부터미널역, 양재역);
                    assertThat(response.getDistance()).isEqualTo(5);
                }
        );
    }
}
