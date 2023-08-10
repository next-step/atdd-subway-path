package nextstep.subway.fixture;

import nextstep.subway.domain.Line;

public class LineFixture {

    public static final String 신분당선_이름 = "신분당선";
    public static final String 분당선_이름 = "분당선";
    public static final String 이호선_이름 = "이호선";
    public static final String 신분당선_색 = "RED";
    public static final String 분당선_색 = "Yellow";
    public static final String 이호선_색 = "Green";
    public static final Integer 거리_10 = 10;

    public static final Line 이호선 = new Line(이호선_이름, 이호선_색);
}
