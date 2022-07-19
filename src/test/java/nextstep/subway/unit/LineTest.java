package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LineTest {

    @Test
    void addSection() {
        // given
        Line line = new Line("신분당선", "red");

        // when
        line.addSection(1L, 2L, 6);

        // then
        List<Section> sections = line.getSections();
        Section section = sections.get(0);
        assertThat(sections).hasSize(1);
        assertThat(section.getUpStationId()).isEqualTo(1L);
        assertThat(section.getDownStationId()).isEqualTo(2L);
    }

    @Test
    void getStations() {
    }

    @Test
    void removeSection() {
    }
}
