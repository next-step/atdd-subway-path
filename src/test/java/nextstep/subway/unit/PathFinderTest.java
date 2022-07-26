package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.PathFinder;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.exception.FindPathException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static nextstep.subway.fixture.ConstStation.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class PathFinderTest {

    private static final String NEW_BUN_DANG = "신분당선";
    private static final String BG_RED_600 = "bg-red-600";
    private static final String BUN_DANG = "분당선";
    private static final String BG_YELLOW_600 = "bg-yellow-600";

    private Line 신분당선;
    private Line 분당선;

    @BeforeEach
    void setUp() {
        신분당선 = Line.of(NEW_BUN_DANG, BG_RED_600);
        분당선 = Line.of(BUN_DANG, BG_YELLOW_600);

        신분당선.addSection(Section.of(신논현역, 강남역, 10));
        신분당선.addSection(Section.of(강남역, 판교역, 15));
        분당선.addSection(Section.of(판교역, 정자역, 20));
        분당선.addSection(Section.of(정자역, 이매역, 25));
    }

    @DisplayName("지하철 경로 조회")
    @Test
    void findPathSourceToTarget() {
        PathFinder pathFinder = PathFinder.of(Arrays.asList(신분당선, 분당선));

        List<Station> path = pathFinder.findPath(신논현역, 이매역);
        double pathWeight = pathFinder.findPathWeight(신논현역, 이매역);

        assertAll(
                () -> assertThat(path).hasSize(5),
                () -> assertThat(path).extracting("name").containsExactly("신논현역", "강남역", "판교역", "정자역", "이매역"),
                () -> assertThat(pathWeight).isEqualTo(70)
        );
    }

    @DisplayName("출발역과 도착역이 같을 경우 예외")
    @Test
    void sameSourceAndTargetStation() {
        PathFinder pathFinder = PathFinder.of(Arrays.asList(신분당선, 분당선));

        assertThatThrownBy(() -> pathFinder.findPath(신논현역, 신논현역))
                .isInstanceOf(FindPathException.class)
                .hasMessage("출발역과 도착역이 같을 수는 없습니다.");
    }

    @DisplayName("출발역과 도착역이 연결되어 있지 않을 경우")
    @Test
    void notConnectedWithSourceAndTargetStation() {
        Line 팔호선 = Line.of("팔호선", "bg-pink-600");
        Station 모란역 = new Station("모란역");
        Station 수진역 = new Station("수진역");
        팔호선.addSection(Section.of(모란역, 수진역, 10));

        PathFinder pathFinder = PathFinder.of(Arrays.asList(신분당선, 분당선, 팔호선));

        assertThatThrownBy(() -> pathFinder.findPath(신논현역, 수진역))
                .isInstanceOf(FindPathException.class);
    }

    @DisplayName("존재하지 않은 출발역을 조회할 경우")
    @Test
    void notExistsStationForSource() {
        Station 모란역 = new Station("모란역");

        PathFinder pathFinder = PathFinder.of(Arrays.asList(신분당선, 분당선));

        assertThatThrownBy(() -> pathFinder.findPath(모란역, 신논현역))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("graph must contain the source vertex");
    }

    @DisplayName("존재하지 않은 도착역을 조회할 경우")
    @Test
    void notExistsStationForTarget() {
        Station 모란역 = new Station("모란역");

        PathFinder pathFinder = PathFinder.of(Arrays.asList(신분당선, 분당선));

        assertThatThrownBy(() -> pathFinder.findPath(신논현역, 모란역))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("graph must contain the sink vertex");
    }
}
