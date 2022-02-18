package nextstep.subway.unit;

import static nextstep.subway.unit.PathFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import java.util.Arrays;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Path;
import nextstep.subway.domain.PathFinder;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class PathFinderTest {

    @DisplayName("최단 거리 조회하기")
    @Test
    void searchPath() {
        // when
        PathFinder pathFinder = PathFinder.from(노선_목록);
        Path path = pathFinder.searchPath(교대역, 양재역);

        // then
        assertThat(path.getStations()).containsExactly(교대역, 남부터미널역, 양재역);
        assertThat(path.getDistance()).isEqualTo(교대역_남부터미널역_거리 + 남부터미널역_양재역_거리);
    }

    @DisplayName("출발역과 도착역이 동일 할 경우")
    @Test
    void searchPathSourceEqualsTarget() {
        // when
        // then
        PathFinder pathFinder = PathFinder.from(노선_목록);
        assertThatIllegalArgumentException().isThrownBy(() -> pathFinder.searchPath(교대역, 교대역))
            .withMessage("출발역과 도착역이 동일합니다.");
    }

    @DisplayName("출발역과 도착역이 연결되어 있지 않을 경우")
    @Test
    void searchPathDoseNotExistShortestPath() {
        // given
        Station 사당역 = new Station("사당역");
        Station 이수역 = new Station("이수역");
        Line 사호선 = Line.of("4호선", "bg-blue-600", 사당역, 이수역, 10);

        // when
        // then
        PathFinder pathFinder = PathFinder.from(Arrays.asList(이호선, 신분당선, 삼호선, 사호선));
        assertThatIllegalArgumentException().isThrownBy(() -> pathFinder.searchPath(교대역, 이수역))
            .withMessage("출발역과 도착역이 연결되어 있지 않습니다.");
    }

    @DisplayName("노선에 존재하지 않은 출발역이나 도착역을 조회 할 경우")
    @Test
    void searchPathSourceDoesNotExistStation() {
        // given
        Station 시청역 = new Station("시청역");
        // when
        // then
        PathFinder pathFinder = PathFinder.from(노선_목록);
        assertThatIllegalArgumentException().isThrownBy(() -> pathFinder.searchPath(교대역, 시청역))
            .withMessage("노선에 포함된 역의 경로만 조회 가능합니다.");
    }

}
