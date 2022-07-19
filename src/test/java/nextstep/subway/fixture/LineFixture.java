package nextstep.subway.fixture;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;

public class LineFixture {
    public static final long  강남역_ID = 1L;
    public static final long  역삼역_ID = 2L;
    public static final long  잠실역_ID = 3L;
    public static final long  신분당선_ID = 1L;
    public static final Station 강남역 = new Station(강남역_ID, "강남역");
    public static final Station 역삼역 = new Station(역삼역_ID, "역삼역");
    public static final Station 잠실역 = new Station(잠실역_ID, "잠실역");
    public static final Section 강남역_역삼역 = new Section(강남역, 역삼역, 10);
    public static final Section 역삼역_잠실역 = new Section(역삼역, 잠실역, 10);
    public static final Line 신분당선 = new Line(신분당선_ID, "신분당선", "red", 강남역_역삼역);

}
