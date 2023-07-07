package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LineTest {
    @Test
    void addSection() {
        Section section = new Section();
        Line line = new Line();
        line.addSection(section);
        assertThat(line.getSections()).contains(section);
    }

    @Test
    void getStations() {
    }

    @Test
    void removeSection() {
    }
}
