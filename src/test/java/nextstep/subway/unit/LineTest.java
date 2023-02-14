package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import nextstep.subway.ui.error.exception.BusinessException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class LineTest {
    @Test
    void addSection() {
        // given
        Line line = new Line("1호선", "파란색");

        // when
        line.addSection(new Station("상행역"), new Station("하행역"), 10);

        // then
        assertThat(line.getSections()).isNotEmpty();
    }

    @Test
    void getStations() {
        // given
        Line line = createLine();

        // when & then
        assertThat(line.getStations()).extracting("name").containsExactly("상행역", "하행역");
    }

    @Test
    void removeSection() {
        // given
        Line line = createLine();
        line.addSection(line.getStations().get(1), new Station("처음역"), 4);
        // when
        line.removeSection(line.getStations().get(1));
        // then
        assertThat(line.getSections().size()).isEqualTo(1);
    }

    @Test
    void removeSectionWithOneSection() {
        // given
        Line line = createLine();

        // when & then
        assertThatThrownBy(() -> line.removeSection(line.getStations().get(1)))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void updateLine() {
        // given
        Line line = createLine();

        // when
        line.updateLine("2호선", "초록색");

        // then
        assertThat(line.getName()).isEqualTo("2호선");
        assertThat(line.getColor()).isEqualTo("초록색");
    }

    private Line createLine() {
        Line line = new Line("1호선", "파란색");
        line.addSection(new Station("상행역"), new Station("하행역"), 10);
        return line;
    }
}
