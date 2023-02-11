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
        Line line = new Line("name", "color");
        line.addSection(new Section(line, new Station("up"), new Station("down"), 20));
        assertThat(line.getStations())
                .extracting(Station::getName)
                .extracting(Name::getName)
                .containsExactly("up", "down");
    }

    @Test
    void getStations() {
        Line line = new Line("name", "color");
        line.addSection(new Section(line, new Station("up"), new Station("down"), 20));
        assertThat(line.getStations())
                .extracting(Station::getName)
                .extracting(Name::getName)
                .containsExactly("up", "down");
    }

    @Test
    void removeSection() {
        Line line = new Line("name", "color");
        Station upStation = new Station("up");
        Station downStation = new Station("down");
        Station downStation2 = new Station("down2");
        line.addSection(new Section(line, upStation, downStation, 20));
        line.addSection(new Section(line, downStation, downStation2, 30));
        line.removeSection(downStation2);

        assertThat(line.getStations())
                .extracting(Station::getName)
                .extracting(Name::getName)
                .containsExactly("up", "down");
    }
}
