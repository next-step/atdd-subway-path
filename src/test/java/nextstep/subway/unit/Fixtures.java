package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;

public class Fixtures {

    public static class LineFixture {

        public static Line line(Long id, String name, String color) {
            if (id == null) {
                id = 1L;
            }
            if (name == null) {
                name = "2호선";
            }
            if (color == null) {
                color = "green";
            }
            return new nextstep.subway.domain.Line(id, name, color);
        }
    }

    public static class SectionFixture {

        public static Section section(Line line, Station upStation, Station downStation,
            int distance, int orderNo) {
            if (upStation == null) {
                upStation = StationFixture.station(1L, "강남역");
            }
            if (downStation == null) {
                downStation = StationFixture.station(2L, "양재역");
            }
            if (line == null) {
                line = LineFixture.line(1L, "2호선", "green");
            }
            if (distance == 0) {
                distance = 10;
            }
            if (orderNo == 0) {
                orderNo = 1;
            }

            return new Section(line, upStation, downStation, distance, orderNo);
        }
    }

    public static class StationFixture {

        public static Station station(Long id, String name) {
            if (id == null) {
                id = 1L;
            }
            if (name == null) {
                name = "강남역";
            }
            return new Station(id, name);
        }
    }
}
