package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;

import nextstep.subway.line.Line;
import nextstep.subway.line.Section;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LineTest {
    @DisplayName("세션을 추가한다")
    @Test
    void addSection() {
        // given
        Line line = new Line();
        Section section = new Section();

        // when
        line.addSection(section);

        // then
        assertThat(line.getSections()).contains(section);
    }

    @Test
    void getStations() {
    }

    @Test
    void removeSection() {
    }
}
