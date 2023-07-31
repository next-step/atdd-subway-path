package nextstep.subway.unit.utils;

import nextstep.subway.domain.line.Line;
import nextstep.subway.domain.station.Station;

public class LineMother {

    public static Line makeLine(String name, String color, Station upStation, Station downStation, int distance) {
        return new Line(name, color, upStation, downStation, distance);
    }

}
