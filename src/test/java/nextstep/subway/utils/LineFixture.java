package nextstep.subway.utils;

import nextstep.subway.line.Line;
import static nextstep.subway.utils.StationFixture.BanghwaStation;
import static nextstep.subway.utils.StationFixture.GangdongStation;

public class LineFixture {
    public static final Long LINE5_ID = 1L;

    public static Line Line5() {
        Line line = Line.builder()
                .name("5호선")
                .color("purple")
                .distance(5)
                .build();

        line.initSection(BanghwaStation(), GangdongStation());
        return line;
    }
}
