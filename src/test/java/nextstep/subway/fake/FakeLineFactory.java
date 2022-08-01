package nextstep.subway.fake;

import nextstep.subway.domain.Line;


public class FakeLineFactory {
    public static Line 신분당선() {
        return new Line("신분당선", "blue");
    }

    public static Line 경의중앙선() {
        return new Line("경의중앙선", "sky");
    }
    public static Line 분당선() {
        return new Line("분당선", "yellow");
    }

    public static Line 이호선() {
        return new Line("2호선", "yellow");
    }

}
