package nextstep.subway.unit;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import nextstep.subway.util.PathFinder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PathFinderTest {
    private Station 교대역;
    private Station 강남역;
    private Station 양재역;
    private Station 선릉역;
    private Station 잠실역;
    private Station 남부터미널역;

    private Line 이호선;
    private Line 삼호선;
    private Line 사호선;
    private Line 신분당선;
    private List<Line> 지하철_노선_목록;

    @BeforeEach
    void init() {
        교대역 = new Station(1L, "교대역");
        강남역 = new Station(2L, "강남역");
        양재역 = new Station(3L, "양재역");
        선릉역 = new Station(4L, "선릉역");
        잠실역 = new Station(5L, "잠실역");
        남부터미널역 = new Station(6L, "남부터미널역");

        이호선 = new Line(1L, "이호선", "green");
        삼호선 = new Line(2L, "삼호선", "orange");
        사호선 = new Line(3L, "사호선", "blue");
        신분당선 = new Line(4L, "신분당선", "blue");
        이호선.addSection(교대역, 강남역, 10);
        삼호선.addSection(교대역, 남부터미널역, 2);
        삼호선.addSection(남부터미널역, 양재역, 3);
        사호선.addSection(선릉역, 잠실역, 5);
        신분당선.addSection(강남역, 양재역, 10);
        지하철_노선_목록 = new ArrayList<>();
        지하철_노선_목록.addAll(List.of(이호선, 삼호선, 사호선, 신분당선));
    }

    @DisplayName("두 지하철역 사이의 경로를 조회한다.")
    @Test
    void findPath() {
        PathResponse 경로 = PathFinder.findPath(지하철_노선_목록, 교대역, 양재역);
        assertThat(경로.getStations()).containsExactly(new StationResponse(1L, "교대역"),
                new StationResponse(6L, "남부터미널역"), new StationResponse(3L, "양재역"));
    }

    @DisplayName("출발역, 도착역이 같은 경우 경로를 조회할 수 없다.")
    @Test
    void findPathEqualStations() {
        assertThatThrownBy(() -> PathFinder.findPath(지하철_노선_목록, 교대역, 교대역))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("도달할 수 없는 역으로의 경로를 조회할 수 없다.")
    @Test
    void findPathToUnreachableStation() {
        assertThatThrownBy(() -> PathFinder.findPath(지하철_노선_목록, 교대역, 잠실역))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
