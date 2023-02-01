package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.fixture.LineFixture;
import org.junit.jupiter.api.Test;

import static nextstep.subway.fixture.LineFixture.*;
import static nextstep.subway.fixture.SectionFixture.createSection;
import static org.assertj.core.api.Assertions.assertThat;

class LineTest {
    @Test
    void addSection() {
        Line line = createLine();
        Section section = createSection(0L, 1L);

        line.addSection(section);

        assertThat(line.getSectionsSize()).isEqualTo(1);
    }
}
