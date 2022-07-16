package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class LineTest {
    @Test
    void 구간을_추가한다() {
        // given
        Line line = new Line("2호선", "green");
        Station upStation = new Station("암사역");
        Station downStation = new Station("모란역");
        Section section = new Section(line, upStation, downStation, 10);

        // when
        line.addSection(section);

        // then
        assertAll(() -> {
            assertThat(line.getSections()).hasSize(1);
            assertThat(line.getSections()).containsExactly(section);
        });
    }

    @Test
    void getStations() {
    }

    @Test
    void removeSection() {
    }
}
