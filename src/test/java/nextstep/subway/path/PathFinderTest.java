package nextstep.subway.path;

import nextstep.subway.line.Line;
import nextstep.subway.line.Lines;
import nextstep.subway.line.section.Section;
import nextstep.subway.station.Station;
import nextstep.subway.testhelper.StationFixture;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PathFinderTest {
    private PathFinder pathFinder;
    private Line 일호선;
    private Line 이호선;
    private Line 삼호선;
    private Line 사호선;
    private Lines lines;

    @BeforeEach
    void setUp() {
        일호선 = new Line("일호선", "blue", StationFixture.잠실역, StationFixture.강남역, 10L);
        이호선 = new Line("이호선", "green", StationFixture.강남역, StationFixture.삼성역, 10L);
        삼호선 = new Line("삼호선", "orange", StationFixture.잠실역, StationFixture.선릉역, 2L);
        사호선 = new Line("삼호선", "orange", StationFixture.교대역, StationFixture.서초역, 5L);
        Section addSection = new Section(
                StationFixture.선릉역,
                StationFixture.삼성역,
                3L);
        삼호선.addSection(addSection);
        pathFinder = new JGraphPathFinder();
        lines = Lines.from(List.of(일호선, 이호선, 삼호선, 사호선));
    }

    @Test
    @DisplayName("Path에서 최단거리의 구간을 찾을 수 있다.")
    void findPath1() {
        Path path = pathFinder.shortcut(lines, StationFixture.잠실역, StationFixture.삼성역);

        List<Station> actualSections = path.getStations();
        List<Station> expectedSections = List.of(StationFixture.잠실역, StationFixture.선릉역, StationFixture.삼성역);
        assertThat(actualSections).isEqualTo(expectedSections);

        Long actualDistance = path.getDistance();
        Long expectedDistance = 5L;
        assertThat(actualDistance).isEqualTo(expectedDistance);
    }

    @Test
    @DisplayName("Path에서 출발역과 도착역이 같을 수 없다")
    void findPath2() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> pathFinder.shortcut(lines, StationFixture.잠실역, StationFixture.잠실역));
    }

    @Test
    @DisplayName("Path에서 출발역과 도착역을 포함하는 라인을 찾지 못했을 경우 에러 발생")
    void findPath3() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> pathFinder.shortcut(lines, StationFixture.강남역, StationFixture.서초역));
    }

    @Test
    @DisplayName("Line에서 역을 찾지 못했을 경우 에러 발생")
    void findPath4() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> pathFinder.shortcut(lines, StationFixture.강남역, StationFixture.오이도역));
    }

}
