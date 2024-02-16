package nextstep.subway.unit;

import nextstep.subway.Exception.LineException;
import nextstep.subway.line.Line;
import nextstep.subway.line.section.Section;
import nextstep.subway.path.PathFinder;
import nextstep.subway.path.PathResponse;
import nextstep.subway.station.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class PathFinderTest {
    private final Station 교대역 = new Station(1L, "교대역");
    private final Station 강남역 = new Station(2L, "강남역");
    private final Station 양재역 = new Station(3L, "양재역");
    private final Station 남부터미널역 = new Station(4L, "남부터미널역");
    private final Line 이호선 = new Line(1L, "이호선", "green", 교대역, 강남역, 10L);
    private final Line 신분당선 = new Line(2L, "신분당선", "red", 강남역, 양재역, 14L);
    private final Line 삼호선 = new Line(3L, "삼호선", "orange", 양재역, 교대역, 23L);
    private final List<Line> LINES = List.of(이호선, 신분당선, 삼호선);
    @BeforeEach
    void setUp() {
        삼호선.addSection(new Section(삼호선, 양재역, 남부터미널역, 5L));
    }

    @DisplayName("최단 경로 조회")
    @Test
    void shortestPath() {
        PathResponse pathResponse = new PathFinder(교대역, 양재역, LINES).shortestPath();

        assertThat(pathResponse.getStations()).containsExactly(교대역, 남부터미널역, 양재역);
        assertThat(pathResponse.getDistance()).isEqualTo(23);
    }

    @DisplayName("에러_최단 경로 조회_출발역 도착역 같음")
    @Test
    void error_shortestPath_target_source_same() {
        assertThatThrownBy(() -> new PathFinder(교대역, 교대역, LINES).shortestPath())
                .isInstanceOf(LineException.class)
                .hasMessage("출발역과 도착역이 같습니다.");
    }
}
