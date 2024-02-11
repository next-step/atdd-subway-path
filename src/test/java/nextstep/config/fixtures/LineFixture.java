package nextstep.config.fixtures;

import nextstep.subway.entity.Line;

public class LineFixture {
    public static Line 이호선_생성() {
        return new Line("이호선", "그린", 10);
    }
}
