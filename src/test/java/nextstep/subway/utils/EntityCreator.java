package nextstep.subway.utils;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;

public class EntityCreator {

    public static Line createLine(String name, String color) {
        return new Line(name, color);
    }

    public static Section createSection(Line line, Station upStation, Station downStation) {
        return new Section(line, upStation, downStation, 10);
    }

    public static Station createStation(String name) {
        return new Station(name);
    }
}
