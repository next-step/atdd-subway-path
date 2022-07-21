package nextstep.subway.fake;

import nextstep.subway.domain.Line;


public class FakeLineFactory {
    public static Line 신분당선() {
        return new Line("신분당선", "blue");
    }
    public static Line 분당선() {
        return new Line("분당선", "yellow");
    }

}
