package nextstep.subway.unit.fixture;

import nextstep.subway.line.repository.Line;

import static nextstep.subway.unit.fixture.SectionFixture.강남역_TO_신논현역;

public class LineFixture {
    public static final Long 신분당선_ID = 1L;

    public static Line 신분당선() {
        return Line
                .builder()
                .name("신분당선")
                .color("bg-red-600")
                .initSection(강남역_TO_신논현역())
                .build();
    }
}
