package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;

public class SubwayFixture {

    public static Station 역_생성(String name) {
        return new Station(name);
    }

    public static Section 구간_생성(Line line, String upStationName, String downStationName, int distance) {
        return new Section(line, 역_생성(upStationName), 역_생성(downStationName), distance);
    }

    public static Line 노선_생성(String name, String color) {
        return new Line(name, color);
    }
}
