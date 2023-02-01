package nextstep.subway.fixture;

import nextstep.subway.domain.Line;

public class LineFixture {

    public static Line createLine() {
        return Line.of("이름", "색깔");
    }
}
