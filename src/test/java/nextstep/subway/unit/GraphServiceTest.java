package nextstep.subway.unit;


import nextstep.subway.domain.GraphService;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class GraphServiceTest {

    private GraphService sut = new GraphService();

    @DisplayName("최단경로를 조회한다.")
    @Test
    void getShortestPath() {
        // given
        var station1 = new Station("신논현역");
        var station2 = new Station("강남역");
        var station3 = new Station("교대역");
        var station4 = new Station("고속터미널역");
        var station5 = new Station("사평역");

        var line1 = new Line("신분당선", "red", station1, station2, 14);
        var line2 = new Line("이호선", "green", station3, station2, 17);
        var line3 = new Line("삼호선", "orange", station4, station3, 16);
        var line4 = new Line("구호선", "yellow", station5, station1, 19);
        line4.addSection(station4, station5, 11);

        // when
        var shortestPath = sut.getShortestPath(station3, station1, List.of(line1, line2, line3, line4));

        // then
        assertThat(shortestPath).containsExactly(station3, station2, station1);
    }

    @DisplayName("최단거리를 조회한다.")
    @Test
    void getShortestDistance() {
        // given
        var station1 = new Station("신논현역");
        var station2 = new Station("강남역");
        var station3 = new Station("교대역");
        var station4 = new Station("고속터미널역");
        var station5 = new Station("사평역");

        var line1 = new Line("신분당선", "red", station1, station2, 14);
        var line2 = new Line("이호선", "green", station3, station2, 17);
        var line3 = new Line("삼호선", "orange", station4, station3, 16);
        var line4 = new Line("구호선", "yellow", station5, station1, 19);
        line4.addSection(station4, station5, 11);

        // when
        var shortestPathDistance = sut.getShortestPathDistance(station3, station1, List.of(line1, line2, line3, line4));

        // then
        assertThat(shortestPathDistance).isEqualTo(31);
    }

    @DisplayName("출발역과 도착역이 같을 때 최단경로를 조회하면 예외가 발생한다.")
    @Test
    void getShortestPathWithSourceEqualToTarget() {
        // given
        var station1 = new Station("신논현역");
        var station2 = new Station("강남역");
        var station3 = new Station("교대역");
        var station4 = new Station("고속터미널역");
        var station5 = new Station("사평역");

        var line1 = new Line("신분당선", "red", station1, station2, 14);
        var line2 = new Line("이호선", "green", station3, station2, 17);
        var line3 = new Line("삼호선", "orange", station4, station3, 16);
        var line4 = new Line("구호선", "yellow", station5, station1, 19);
        line4.addSection(station4, station5, 11);

        // when, then
        assertThrows(RuntimeException.class,
                () -> sut.getShortestPath(station3, station3, List.of(line1, line2, line3, line4))
        );
    }

    @DisplayName("출발역이나 도착역이 존재하지 않을때 최단경로를 조회하면 예외가 발생한다.")
    @Test
    void getShortestPathWithNotExistStation() {
        // given
        var station1 = new Station("신논현역");
        var station2 = new Station("강남역");
        var station3 = new Station("교대역");
        var station4 = new Station("고속터미널역");
        var station5 = new Station("사평역");

        var line1 = new Line("신분당선", "red", station1, station2, 14);
        var line2 = new Line("이호선", "green", station3, station2, 17);
        var line3 = new Line("삼호선", "orange", station4, station3, 16);
        var line4 = new Line("구호선", "yellow", station5, station1, 19);
        line4.addSection(station4, station5, 11);

        // when, then
        assertThrows(RuntimeException.class,
                () -> sut.getShortestPath(station3, new Station("판교역"), List.of(line1, line2, line3, line4))
        );
    }

    @DisplayName("최단경로가 존재하지 않는 요청은 예외가 발생한다")
    @Test
    void getNotExistShortestPath() {
        // given
        var station1 = new Station("신논현역");
        var station2 = new Station("강남역");
        var station3 = new Station("교대역");
        var station4 = new Station("고속터미널역");

        var line1 = new Line("신분당선", "red", station1, station2, 14);
        var line3 = new Line("삼호선", "orange", station4, station3, 16);

        // when, then
        assertThrows(RuntimeException.class,
                () -> sut.getShortestPath(station3, station1, List.of(line1, line3))
        );
    }
}
