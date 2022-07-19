package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LineTest {

    @DisplayName("구간 등록을 할 수 있다.")
    @Test
    void addSection() {
        // given
        final var upStation = new Station("선릉역");
        final var downStation = new Station("삼성역");
        final var line = new Line("2호선", "bg-green-600");
        final var section = new Section(line, upStation, downStation, 10);

        // when
        Line sectionAddedLine = line.addSection(section);

        // then
        assertThat(sectionAddedLine.getSections()).containsExactly(section);
    }

    @Test
    void getStations() {
    }

    @Test
    void removeSection() {
    }
}
