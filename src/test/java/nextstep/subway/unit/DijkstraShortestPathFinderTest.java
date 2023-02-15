package nextstep.subway.unit;

import nextstep.subway.applicaion.DijkstraShortestPathFinder;
import nextstep.subway.common.exception.NoPathConnectedException;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Path;
import nextstep.subway.domain.PathResult;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static nextstep.subway.common.error.SubwayError.NO_PATH_CONNECTED;
import static nextstep.subway.unit.TestFixtureLine.노선_생성;
import static nextstep.subway.unit.TestFixtureStation.역_생성;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class DijkstraShortestPathFinderTest {

    private Station 교대역;
    private Station 강남역;
    private Station 양재역;
    private Station 남부터미널역;
    private Station 부평역;
    private Station 검암역;
    private Line 이호선;
    private Line 신분당선;
    private Line 삼호선;
    private Line 일호선;

    /**
     * 교대역    --- *2호선* ---   강남역
     * |                        |
     * *3호선*                   *신분당선*
     * |                        |
     * 남부터미널역  --- *3호선* ---   양재
     */
    @BeforeEach
    public void setUp() {

        교대역 = 역_생성(1L, "교대역");
        강남역 = 역_생성(2L, "강남역");
        양재역 = 역_생성(3L, "양재역");
        남부터미널역 = 역_생성(4L, "남부터미널역");
        부평역 = 역_생성(5L, "부평역");
        검암역 = 역_생성(6L, "검암역");

        이호선 = 노선_생성(1L, "2호선", "green", 교대역, 강남역, 10);
        신분당선 = 노선_생성(2L, "신분당선", "red", 강남역, 양재역, 10);
        삼호선 = 노선_생성(3L, "3호선", "orange", 교대역, 남부터미널역, 2);
        삼호선.addSection(남부터미널역, 양재역, 3);
        일호선 = 노선_생성(4L, "1호선", "blue", 부평역, 검암역, 5);
    }

    @DisplayName("최적의 경로를 찾는다.")
    @Test
    void findRouteTest() {

        final DijkstraShortestPathFinder pathFinder = new DijkstraShortestPathFinder();
        final List<Line> 전체_노선_목록 = List.of(이호선, 신분당선, 삼호선);
        final Path 경로 = Path.of(전체_노선_목록, 교대역, 양재역);
        final PathResult 경로_결과 = pathFinder.findRoute(경로);

        assertAll(
                () -> assertThat(경로_결과.getStations()).hasSize(3),
                () -> assertThat(경로_결과.getDistance()).isEqualTo(5)
        );
    }

    @DisplayName("출발역과 도착역이 연결되어 있지 않아서 조회가 불가능합니다.")
    @Test
    void error_notConnectedSourceNTarget() {

        final DijkstraShortestPathFinder pathFinder = new DijkstraShortestPathFinder();
        final List<Line> 전체_노선_목록 = List.of(이호선, 신분당선, 삼호선, 일호선);
        final Path 경로 = Path.of(전체_노선_목록, 교대역, 부평역);

        assertThatThrownBy(() -> pathFinder.findRoute(경로))
                .isInstanceOf(NoPathConnectedException.class)
                .hasMessage(NO_PATH_CONNECTED.getMessage());
    }
}