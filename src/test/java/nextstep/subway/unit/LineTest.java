package nextstep.subway.unit;

import nextstep.subway.line.Color;
import nextstep.subway.line.Line;
import nextstep.subway.section.Section;
import nextstep.subway.station.Station;
import org.junit.jupiter.api.Test;


import static org.assertj.core.api.Assertions.assertThat;

class LineTest {
    @Test
    void addSection() {

        Station 건대입구역 = new Station("건대입구역");
        Station 구의역 = new Station("구의역");
        Station 강변역 = new Station("강변역");

        Line line = new Line("2호선", Color.GREEN, 건대입구역, 구의역, 6);
        Section section = new Section(구의역, 강변역, 4, line.getId());

        line.addSection(section);

        assertThat(line.getDownStation()).isEqualTo(강변역);
    }

    @Test
    void getStations() {

        Station 건대입구역 = new Station("건대입구역");
        Station 구의역 = new Station("구의역");
        Station 강변역 = new Station("강변역");

        Line line = new Line("2호선", Color.GREEN, 건대입구역, 구의역, 6);
        Section section = new Section(구의역, 강변역, 4, line.getId());

        line.addSection(section);

        assertThat(line.getStations()).containsExactly(건대입구역, 강변역);
    }

    @Test
    void removeSection() {

        Station 건대입구역 = new Station("건대입구역");
        Station 구의역 = new Station("구의역");
        Station 강변역 = new Station("강변역");

        Line line = new Line("2호선", Color.GREEN, 건대입구역, 구의역, 6);
        Section section = new Section(구의역, 강변역, 4, line.getId());
        line.addSection(section);

        line.removeSection(section);

        assertThat(line.getDownStation()).isEqualTo(구의역);
    }
}
