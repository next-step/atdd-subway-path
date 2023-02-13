package nextstep.subway.unit;

import nextstep.subway.domain.line.Line;
import nextstep.subway.domain.section.Section;
import nextstep.subway.domain.station.Name;
import nextstep.subway.domain.station.Station;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LineTest {
    @Test
    void addSection() {
        Line line = new Line(1L, "name", "color");
        line.addSection(new Section(1L, line, new Station(1L, "up"), new Station(2L, "down"), 20));
        assertThat(line.getStations())
                .extracting(Station::getName)
                .extracting(Name::getName)
                .containsExactly("up", "down");
    }

    @Test
    void getStations() {
        Line line = new Line(1L, "name", "color");
        line.addSection(new Section(1L, line, new Station(1L, "up"), new Station(2L, "down"), 20));
        assertThat(line.getStations())
                .extracting(Station::getName)
                .extracting(Name::getName)
                .containsExactly("up", "down");
    }

    @Test
    void removeSection() {
        Line line = new Line(1L, "name", "color");
        Station upStation = new Station(1L, "up");
        Station downStation = new Station(2L, "down");
        Station downStation2 = new Station(3L, "down2");
        line.addSection(new Section(1L, line, upStation, downStation, 20));
        line.addSection(new Section(2L, line, downStation, downStation2, 30));
        line.removeSection(downStation2);

        assertThat(line.getStations())
                .extracting(Station::getName)
                .extracting(Name::getName)
                .containsExactly("up", "down");
    }
}
