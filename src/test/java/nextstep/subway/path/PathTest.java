package nextstep.subway.path;

import nextstep.subway.line.Line;
import nextstep.subway.line.section.Section;
import nextstep.subway.testhelper.StationFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

public class PathTest {

    private Path path;

    @BeforeEach
    void setUp() {
        Line 일호선 = new Line("일호선", "blue", StationFixture.잠실역, StationFixture.강남역, 10L);
        Line 이호선 = new Line("일호선", "blue", StationFixture.잠실역, StationFixture.삼성역, 10L);
        Line 삼호선 = new Line("일호선", "blue", StationFixture.강남역, StationFixture.선릉역, 2L);
        Section addSection = new Section(
                StationFixture.선릉역,
                StationFixture.삼성역,
                2L);
        삼호선.addSection(addSection);
        path = new Path(List.of(일호선, 이호선, 삼호선));
    }

    @Test
    void test() {
    }
}
