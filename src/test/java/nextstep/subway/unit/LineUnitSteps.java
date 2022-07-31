package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;

public class LineUnitSteps {

    public static void 노선_구간_추가(final Line line, final Station upStation, final Station downStation, final int distance) {
        line.addSections(upStation, downStation, distance);
    }

}
