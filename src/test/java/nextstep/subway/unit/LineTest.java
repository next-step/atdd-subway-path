package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LineTest {
    @Test
    void addSection() {

        // given
        Line line = new Line();
        String upStationName = "up";
        String downStationName = "down";
        Station upStation = new Station(upStationName);
        Station downStation = new Station(downStationName);

        // when
        line.addSection(upStation, downStation, 10);

        // then
        assertThat(line.getSections()).hasSize(1);
        assertThat(line.getSections().get(0).getUpStation().getName()).isEqualTo(upStationName);
        assertThat(line.getSections().get(0).getDownStation().getName()).isEqualTo(downStationName);
    }

    @Test
    void getStations() {
    }

    @Test
    void removeSection() {
    }


    @Test
    void getSection() {

        // given
        Line line = new Line();
        Station upStation1 = new Station(1L, "up1");
        Station downStation1 = new Station(2L, "down1");
        Station upStation2 = new Station(3L, "up2");
        Station downStation2 = new Station(4L, "down2");

        line.addSection(upStation1, downStation1, 10);
        line.addSection(upStation2, downStation2, 10);

        // when
        Section section = line.getSection(upStation1.getId(), downStation1.getId());

        // then
        assertThat(section.getLine()).isEqualTo(line);
        assertThat(section.getUpStation()).isEqualTo(upStation1);
        assertThat(section.getDownStation()).isEqualTo(downStation1);
    }
}
