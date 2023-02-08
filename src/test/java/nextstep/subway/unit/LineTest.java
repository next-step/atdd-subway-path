package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.Test;

class LineTest {
    @Test
    void addSection() {
        Line line = new Line("2호선", "초록색");
        Section section = createTestSection(line);
        line.addSections(section);

        assertThat(line.getSections().size()).isEqualTo(1);
    }

    @Test
    void getStations() {
        Line line = new Line("2호선", "초록색");
        line.addSections(createTestSection(line));
        assertThat(line.getStations().size()).isEqualTo(0);
    }

    @Test
    void removeSection() {
        Line line = new Line("2호선", "초록색");
        Section section = createTestSection(line);
        line.addSections(section);

        line.removeSections(section);

        assertThat(line.getSections().size()).isEqualTo(0);
    }

    private Section createTestSection(Line line) {
        Station upStation = new Station("서울대입구역");
        Station downStation = new Station("봉천역");
        return new Section(line, upStation, downStation,10);
    }
}
