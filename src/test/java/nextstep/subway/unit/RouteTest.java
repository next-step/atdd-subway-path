package nextstep.subway.unit;

import nextstep.subway.line.Line;
import nextstep.subway.line.Section;
import nextstep.subway.path.Route;
import nextstep.subway.station.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class RouteTest {

    /**
     * 교대역    -- 4 -- *2호선* ---   강남역
     * |                                    |
     * *3호선*                         *신분당선*
     * *2*                                 *5*
     * |                                    |
     * 남부터미널역  -- 3 -- *3호선* ---   양재
     */
    private Station 교대역;
    private Station 강남역;
    private Station 양재역;
    private Station 남부터미널역;
    private Station 노원역;
    private Station 상계역;

    private Line 이호선;
    private Line 신분당선;
    private Line 삼호선;
    private Line 사호선;

    private Route route = new Route();

    @BeforeEach
    void setUp() {
        교대역 = new Station(1L, "교대역");
        강남역 = new Station(2L, "강남역");
        양재역 = new Station(3L, "양재역");
        남부터미널역 = new Station(4L, "남부터미널역");
        노원역 = new Station(5L, "노원역");
        상계역 = new Station(6L, "상계역");

        이호선 = new Line("2호선", "green", 교대역, 강남역, 4L);
        신분당선 = new Line("신분당선", "red", 강남역, 양재역, 5L);
        삼호선 = new Line("3호선", "orange", 교대역, 남부터미널역, 2L);
        사호선 = new Line("4호선", "blue", 노원역, 상계역, 1L);

        삼호선.addNewSection(남부터미널역, 양재역, 3L);

        route.initGraph(
                List.of(
                        new Section(이호선, 교대역, 강남역, 4L),
                        new Section(신분당선, 강남역, 양재역, 5L),
                        new Section(삼호선, 교대역, 남부터미널역, 2L),
                        new Section(삼호선, 남부터미널역, 양재역, 3L),
                        new Section(사호선, 노원역, 상계역, 1L)
                )
        );
    }

    @DisplayName("최단 거리 조회 기능")
    @Nested
    class FindShortestDistance {

        @DisplayName("최단 거리 조회 성공")
        @Test
        void findShortestDistance() {
            // when
            int distance = route.findShortestDistance(교대역, 양재역);

            // then
            assertThat(distance).isEqualTo(5);
        }

        @DisplayName("최단 거리 조회 기능 - 출발역과 도착역이 같은 경우 에러가 발생한다.")
        @Test
        void findShortestDistanceWithSameStation() {
            // when & then
            assertThatThrownBy(() -> route.findShortestDistance(교대역, 교대역))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("출발역과 도착역이 같습니다. stationId: 1");
        }

        @DisplayName("최단 거리 조회 기능 - 출발역과 도착역이 연결되어 있지 않은 경우 에러가 발생한다.")
        @Test
        void findShortestDistanceWithNotConnectStation() {
            // when & then
            assertThatThrownBy(() -> route.findShortestDistance(교대역, 노원역))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("출발역과 도착역이 연결되어 있지 않습니다. sourceStationId: 1, targetStationId: 5");
        }

        @DisplayName("최단 거리 조회 기능 - 출발역이 존재하지 않는 경우 에러가 발생한다.")
        @Test
        void findShortestDistanceWithNotExistSourceStation() {
            // when & then
            assertThatThrownBy(() -> route.findShortestDistance(new Station(100L, "잠실역"), 양재역))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("출발역이 존재하지 않습니다. stationId: 100");
        }

        @DisplayName("최단 거리 조회 기능 - 도착역이 존재하지 않는 경우 에러가 발생한다.")
        @Test
        void findShortestDistanceWithNotExistTargetStation() {
            // when & then
            assertThatThrownBy(() -> route.findShortestDistance(교대역, new Station(100L, "잠실역")))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("도착역이 존재하지 않습니다. stationId: 100");
        }
    }

    @DisplayName("최단 경로 조회 기능")
    @Nested
    class FindShortestPath {

        @DisplayName("최단 경로 조회 성공")
        @Test
        void findShortestPath() {
            // when
            List<Station> stations = route.findShortestPath(교대역, 양재역);

            // then
            assertThat(stations).containsExactly(교대역, 남부터미널역, 양재역);
        }

        @DisplayName("최단 경로 조회 기능 - 출발역과 도착역이 같은 경우 에러가 발생한다.")
        @Test
        void findShortestPathWithSameStation() {
            // when & then
            assertThatThrownBy(() -> route.findShortestPath(교대역, 교대역))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("출발역과 도착역이 같습니다. stationId: 1");
        }

        @DisplayName("최단 경로 조회 기능 - 출발역과 도착역이 연결되어 있지 않은 경우 에러가 발생한다.")
        @Test
        void findShortestPathWithNotConnectStation() {
            // when & then
            assertThatThrownBy(() -> route.findShortestPath(교대역, 노원역))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("출발역과 도착역이 연결되어 있지 않습니다. sourceStationId: 1, targetStationId: 5");
        }

        @DisplayName("최단 경로 조회 기능 - 출발역이 존재하지 않는 경우 에러가 발생한다.")
        @Test
        void findShortestPathWithNotExistSourceStation() {
            // when & then
            assertThatThrownBy(() -> route.findShortestPath(new Station(100L, "잠실역"), 양재역))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("출발역이 존재하지 않습니다. stationId: 100");
        }

        @DisplayName("최단 경로 조회 기능 - 도착역이 존재하지 않는 경우 에러가 발생한다.")
        @Test
        void findShortestPathWithNotExistTargetStation() {
            // when & then
            assertThatThrownBy(() -> route.findShortestPath(교대역, new Station(100L, "잠실역")))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("도착역이 존재하지 않습니다. stationId: 100");
        }
    }
}
