package nextstep.subway.path;

import nextstep.subway.line.Line;
import nextstep.subway.line.section.Section;
import nextstep.subway.line.section.Sections;
import nextstep.subway.testhelper.StationFixture;
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

    @BeforeEach
    void setUp() {
        일호선 = new Line("일호선", "blue", StationFixture.잠실역, StationFixture.강남역, 10L);
        이호선 = new Line("일호선", "blue", StationFixture.잠실역, StationFixture.삼성역, 10L);
        삼호선 = new Line("일호선", "blue", StationFixture.강남역, StationFixture.선릉역, 2L);
        Section addSection = new Section(
                StationFixture.선릉역,
                StationFixture.삼성역,
                2L);
        삼호선.addSection(addSection);
        pathFinder = new PathFinder(List.of(일호선, 이호선, 삼호선));
    }

    @Test
    @DisplayName("Path에서 최단거리의 구간을 찾을 수 있다.")
    void findPath() {
        Path path = pathFinder.shortcut();

        Sections actuaclSections = path.getSections();
        Sections expectedSections = 삼호선.getSections();
        assertThat(actuaclSections).isEqualTo(expectedSections);

        Long actualDistance = path.getDistance();
        Long expectedDistance = 5L;
        assertThat(actualDistance).isEqualTo(expectedDistance);
    }
}
