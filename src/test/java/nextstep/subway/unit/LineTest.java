package nextstep.subway.unit;

import nextstep.subway.line.domain.Color;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.Section;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.Test;


import static org.assertj.core.api.Assertions.assertThat;

class LineTest {
    @Test
    void addSection() {

        Station 건대입구역 = new Station("건대입구역");
        Station 구의역 = new Station("구의역");
        Station 강변역 = new Station("강변역");

        Line line = new Line("2호선", Color.GREEN, 건대입구역, 구의역, 6);
        line.addSection(new Section(구의역, 강변역, 4, line));

        assertThat(line.getStations()).containsExactly(건대입구역, 구의역, 강변역);
    }

    @Test
    void getStations() {

        Station 건대입구역 = new Station("건대입구역");
        Station 구의역 = new Station("구의역");
        Station 강변역 = new Station("강변역");

        Line line = new Line("2호선", Color.GREEN, 건대입구역, 구의역, 6);
        Section section2 = new Section(구의역, 강변역, 4, line);
        line.addSection(section2);

        assertThat(line.getStations()).containsExactly(건대입구역, 구의역, 강변역);
    }

    @Test
    void removeSection() {

        // given
        Station 건대입구역 = new Station("건대입구역");
        Station 구의역 = new Station("구의역");
        Station 강변역 = new Station("강변역");

        Line line = new Line("2호선", Color.GREEN, 건대입구역, 구의역, 6);
        Section section2 = new Section(구의역, 강변역, 4, line);
        line.addSection(section2);

        // when
        line.removeSection(section2);

        // then
        assertThat(line.getDownStation()).isEqualTo(구의역);
    }
}
