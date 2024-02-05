package nextstep.subway.unit;

import nextstep.subway.line.Line;
import nextstep.subway.line.section.Section;
import nextstep.subway.station.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


class LineTest {

    private Line line = new Line("2호선", "green");;
    private Station upStation = new Station("강남역");;
    private Station downStation = new Station("역삼역");;
    private Section section = new Section(upStation, downStation, 10, line);;

    @BeforeEach
    void beforeEach(){
        line.getSections().clear();
    }


    @Test
    void addSection() {
        line.getSections().add(section);
        assertThat(line.getSections()).contains(section);
    }

    @Test
    void getStations() {
        line.getSections().add(section);
        assertThat(line.getSections()).contains(section);
    }

    @Test
    void removeSection() {
        line.getSections().add(section);
        line.removeSection(section);
        assertThat(line.getSections()).hasSize(0);
    }
}
