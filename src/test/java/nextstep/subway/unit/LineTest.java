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
        long upStationId = 1L;
        long downStationId = 2L;

        Line line = new Line("신분당선", "red");

        // when
        line.addSection(upStationId, downStationId, 6);

        // then
        List<Section> sections = line.getSections();
        assertThat(sections).hasSize(1);

        Section addedSection = sections.get(0);
        assertThat(addedSection.getUpStationId()).isEqualTo(upStationId);
        assertThat(addedSection.getDownStationId()).isEqualTo(downStationId);
    }
}
