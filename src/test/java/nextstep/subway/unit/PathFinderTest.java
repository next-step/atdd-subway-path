package nextstep.subway.unit;

import nextstep.subway.line.Line;
import nextstep.subway.line.section.Section;
import nextstep.subway.path.PathFinder;
import nextstep.subway.path.PathResponse;
import nextstep.subway.station.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class PathFinderTest {

    @DisplayName("최단거리 구하기")
    @Test
    void shortestPath() {
        Station 교대역 = new Station(1L, "교대역");
        Station 강남역 = new Station(2L, "강남역");
        Station 양재역 = new Station(3L, "양재역");
        Station 남부터미널역 = new Station(4L, "남부터미널역");
        Line 이호선 = new Line(1L, "이호선", "green", 교대역, 강남역, 10L);
        Line 신분당선 = new Line(2L, "신분당선", "red", 강남역, 양재역, 14L);
        Line 삼호선 = new Line(3L, "삼호선", "orange", 양재역, 교대역, 23L);
        삼호선.addSection(new Section(삼호선, 양재역, 남부터미널역, 5L));

        PathResponse pathResponse = new PathFinder().shortestPath(교대역, 양재역, List.of(이호선, 신분당선, 삼호선));

        assertThat(pathResponse.getStations()).containsExactly(교대역, 남부터미널역, 양재역);
        assertThat(pathResponse.getDistance()).isEqualTo(23);
    }
}
