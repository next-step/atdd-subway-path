package nextstep.subway.domain.factory;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;

public class EntityFactory {
    public static Line createLine(String name, String color, Station upStation, Station downStation, int distance) {
        return Line.of(name, color, upStation, downStation, distance);
    }

    public static Station createStation(Long id, String stationName) {
        return new Station(id, stationName);
    }

    public static Section createSection(Line line, Station upStation, Station downStation, int distance) {
        return Section.of(line, upStation, downStation, distance);
    }
}
