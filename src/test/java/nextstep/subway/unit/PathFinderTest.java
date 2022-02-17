package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import java.util.Arrays;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Path;
import nextstep.subway.domain.PathFinder;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class PathFinderTest {
    private Station 강남역;
    private Station 교대역;
    private Station 양재역;
    private Station 남부터미널역;

    private Line 이호선;
    private Line 삼호선;
    private Line 신분당선;

    @BeforeEach
    void setUp() {
        강남역 = new Station("강남역");
        교대역 = new Station("교대역");
        이호선 = Line.of("2호선", "bg-green-600", 교대역, 강남역, 10);

        양재역 = new Station("양재역");
        신분당선 = Line.of("신분당선", "bg-red-500", 강남역, 양재역, 5);

        남부터미널역 = new Station("남부터미널역");
        삼호선 = Line.of("3호선", "bg-orange-500", 교대역, 남부터미널역, 5);
        삼호선.addSection(남부터미널역, 양재역, 15);
    }

    @DisplayName("최단 거리 조회하기")
    @Test
    void searchPath() {
        // when
        PathFinder pathFinder = PathFinder.of(Arrays.asList(이호선, 신분당선, 삼호선));
        Path path = pathFinder.searchPath(교대역, 양재역);

        // then
        assertThat(path.getStations()).containsExactly(교대역, 강남역, 양재역);
        assertThat(path.getDistance()).isEqualTo(15);
    }

    @DisplayName("출발역과 도착역이 동일 할 경우")
    @Test
    void searchPathSourceEqualsTarget() {
        // when
        // then
        PathFinder pathFinder = PathFinder.of(Arrays.asList(이호선, 신분당선, 삼호선));
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
        PathFinder pathFinder = PathFinder.of(Arrays.asList(이호선, 신분당선, 삼호선, 사호선));
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
        PathFinder pathFinder = PathFinder.of(Arrays.asList(이호선, 신분당선, 삼호선));
        assertThatIllegalArgumentException().isThrownBy(() -> pathFinder.searchPath(교대역, 시청역))
            .withMessage("노선에 포함된 역의 경로만 조회 가능합니다.");
    }

}
