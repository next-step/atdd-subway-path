package nextstep.subway.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

class PathFinderTest {

    private Station 교대역;
    private Station 강남역;
    private Station 양재역;
    private Station 남부터미널역;

    private Line 이호선;
    private Line 신분당선;
    private Line 삼호선;

    private int 교대역_강남역 = 10;
    private int 강남역_양재역 = 10;
    private int 교대역_남부터미널역 = 2;
    private int 남부터미널역_양재역 = 19;

    /**
     * 교대역    --- *2호선* ---   강남역
     * |                        |
     * *3호선*                   *신분당선*
     * |                        |
     * 남부터미널역  --- *3호선* ---   양재역
     */
    @BeforeEach
    void setUp() {
        교대역 = new Station(1L, "교대역");
        강남역 = new Station(2L, "강남역");
        양재역 = new Station(3L, "양재역");
        남부터미널역 = new Station(4L, "남부터미널역");

        이호선 = new Line(1L, "2호선", "green", 교대역, 강남역, 교대역_강남역);
        신분당선 = new Line(1L, "신분당선", "red", 강남역, 양재역, 강남역_양재역);
        삼호선 = new Line(1L, "3호선", "orange", 교대역, 남부터미널역, 교대역_남부터미널역);

        삼호선.addSection(남부터미널역, 양재역, 남부터미널역_양재역);
    }

    @DisplayName("최단 구간의 역 목록을 반환")
    @Test
    void getShortestPathStations() {
        // when
        PathFinder pathFinder = new PathFinder(List.of(이호선, 신분당선, 삼호선));

        // then
        assertThat(pathFinder.getShortestPathStations(교대역, 양재역)).isEqualTo(List.of(교대역, 강남역, 양재역));
    }

    @DisplayName("연결이 되어 있지 않은 역으로 구간의 역 목록을 반환 시 예외")
    @Test
    void getShortestPathStationsWithUnconnectedStation() {
        // when
        PathFinder pathFinder = new PathFinder(List.of(이호선, 신분당선, 삼호선));
        Station unconnectedStation = new Station(999L, "사당역");

        // then
        assertThatIllegalArgumentException().isThrownBy(() -> pathFinder.getShortestPathStations(교대역, unconnectedStation))
                .withMessage("연결이 되어 있지 않은 구간 입니다.");
    }

    @DisplayName("최단 구간의 거리를 반환")
    @Test
    void getShortestPathDistance() {
        // when
        PathFinder pathFinder = new PathFinder(List.of(이호선, 신분당선, 삼호선));

        // then
        assertThat(pathFinder.getShortestPathDistance(교대역, 양재역)).isEqualTo(교대역_강남역 + 강남역_양재역);
    }

    @DisplayName("연결이 되어 있지 않은 역으로 구간의 거리를 반환 시 예외")
    @Test
    void getShortestPathDistanceWithUnconnectedStation() {
        // when
        PathFinder pathFinder = new PathFinder(List.of(이호선, 신분당선, 삼호선));
        Station unconnectedStation = new Station(999L, "사당역");

        // then
        assertThatIllegalArgumentException().isThrownBy(() -> pathFinder.getShortestPathDistance(교대역, unconnectedStation))
                .withMessage("연결이 되어 있지 않은 구간 입니다.");
    }
}
