package nextstep.subway.domain.factory;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;

public class EntityFactory {
    public static Line createMockLine(Long id, String name, String color, Station upStation, Station downStation, int distance) {
        return Line.of(id, name, color, upStation, downStation, distance);
    }

    public static Line createLine(String name, String color, Station upStation, Station downStation, int distance) {
        return Line.of(name, color, upStation, downStation, distance);
    }

    public static Station createMockStation(Long id, String stationName) {
        return new Station(id, stationName);
    }

    public static Station createStation(String stationName) {
        return Station.from(stationName);
    }

    public static Section createSection(Line line, Station upStation, Station downStation, int distance) {
        return Section.of(line, upStation, downStation, distance);
    }
}
