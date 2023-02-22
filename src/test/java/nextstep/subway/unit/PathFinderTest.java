package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.PathFinder;
import nextstep.subway.domain.ShortestPath;
import nextstep.subway.domain.Station;
import nextstep.subway.exception.PathOriginSameAsTargetException;
import nextstep.subway.exception.PathTargetNotLinkedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static nextstep.subway.fixture.LineFixture.삼호선;
import static nextstep.subway.fixture.LineFixture.신분당선;
import static nextstep.subway.fixture.LineFixture.이호선;
import static nextstep.subway.fixture.SectionFixture.강남_양재_구간;
import static nextstep.subway.fixture.SectionFixture.교대_강남_구간;
import static nextstep.subway.fixture.SectionFixture.교대_남부터미널_구간;
import static nextstep.subway.fixture.SectionFixture.남부터미널_양재_구간;
import static nextstep.subway.fixture.StationFixture.강남역;
import static nextstep.subway.fixture.StationFixture.교대역;
import static nextstep.subway.fixture.StationFixture.남부터미널역;
import static nextstep.subway.fixture.StationFixture.범계역;
import static nextstep.subway.fixture.StationFixture.양재역;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class PathFinderTest {

    private Line 이_호선;
    private Line 삼_호선;
    private Line 신분당_선;

    private Station 남부터미널_역;
    private Station 교대_역;
    private Station 강남_역;
    private Station 양재_역;

    /**
     *                  (10)
     *   교대역    ---- *2호선*  ---   강남역
     *     |                           |
     *    (2)                         (8)
     *   *3호선*                    *신분당선*
     *     |                          |
     * 남부터미널역   --- *3호선* ---   양재
     *                  (3)
     */
    @BeforeEach
    void setUp() {
        이_호선 = 이호선.엔티티_생성();
        삼_호선 = 삼호선.엔티티_생성();
        신분당_선 = 신분당선.엔티티_생성();

        남부터미널_역 = 남부터미널역.엔티티_생성(1L);
        교대_역 = 교대역.엔티티_생성(2L);
        강남_역 = 강남역.엔티티_생성(3L);
        양재_역 = 양재역.엔티티_생성(4L);

        삼_호선.addSection(교대_남부터미널_구간.엔티티_생성(삼_호선, 교대_역, 남부터미널_역));
        삼_호선.addSection(남부터미널_양재_구간.엔티티_생성(삼_호선, 남부터미널_역, 양재_역));
        이_호선.addSection(교대_강남_구간.엔티티_생성(이_호선, 교대_역, 강남_역));
        신분당_선.addSection(강남_양재_구간.엔티티_생성(신분당_선, 강남_역, 양재_역));
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class 경로_탐색 {

        private PathFinder pathFinder;

        @BeforeEach
        void setUp() {
            pathFinder = new PathFinder(List.of(이_호선, 삼_호선, 신분당_선));
        }

        @Nested
        @DisplayName("교대역에서 양재역까지의 최단 경로를 탐색하면")
        class Context_with_find_shortest_path {

            @Test
            @DisplayName("총 구간 거리와 최단 경로의 역 목록들을 순서대로 반환한다")
            void it_returns_stations_and_tatal_distance() throws Exception {
                ShortestPath shortestPathWithDijkstra = pathFinder.findShortestPathWithDijkstra(교대_역, 양재_역);

                assertAll(
                        () -> assertThat(shortestPathWithDijkstra.getTotalDistance())
                                .isEqualTo(교대_남부터미널_구간.구간_거리() + 남부터미널_양재_구간.구간_거리()),
                        () -> assertThat(shortestPathWithDijkstra.getStations())
                                .containsExactly(교대_역, 남부터미널_역, 양재_역)
                );
            }
        }

        @Nested
        @DisplayName("출발역과 도착역이 같은 경우")
        class Context_with_source_and_target_same {

            @Test
            @DisplayName("PathOriginSameAsTargetException 예외를 던진다")
            void it_returns_exception() throws Exception {
                assertThatThrownBy(() -> pathFinder.findShortestPathWithDijkstra(교대_역, 교대_역))
                        .isInstanceOf(PathOriginSameAsTargetException.class);
            }
        }

        @Nested
        @DisplayName("출발역과 도착역이 연결이 되어 있지 않은 경우")
        class Context_with_target_station_not_linked_with_origin_station {

            private Station 연결되지_않은_역 = 범계역.엔티티_생성(5L);

            @Test
            @DisplayName("PathTargetNotLinkedException 예외를 던진다")
            void it_returns_exception() throws Exception {
                assertThatThrownBy(() -> pathFinder.findShortestPathWithDijkstra(교대_역, 연결되지_않은_역))
                        .isInstanceOf(PathTargetNotLinkedException.class);
            }
        }
    }
}
