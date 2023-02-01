package nextstep.subway.fixture;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;

import static nextstep.subway.fixture.SectionFixture.createSection;

public class LineFixture {

    public static Line createLine() {
        return Line.of("이름", "색깔");
    }

    public static Line createLine(Long upStationId, Long downStationId) {
        Line line = createLine();
        Section section = createSection(0L, 1L);
        line.addSection(section);
        return line;
    }
}
