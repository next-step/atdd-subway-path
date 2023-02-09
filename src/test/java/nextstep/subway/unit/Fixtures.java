package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;

public class Fixtures {
    public static final Station 판교역 = createStation(1L, "판교역");
    public static final Station 정자역 = createStation(2L, "정자역");
    public static final Station 미금역 = createStation(3L, "미금역");

    public static Station createStation(Long id, String name) {
        return new Station(id, name);
    }

    public static Section createSection(Long id, Line line, Station upStation, Station downStation, int distance) {
        return new Section(id, line, upStation, downStation, distance);
    }
}
