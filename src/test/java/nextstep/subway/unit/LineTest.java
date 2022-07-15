package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class LineTest {

    @Test
    void addSection() {
        // given
        final Line line = new Line();

        // when
        line.addSection(mock(Station.class), mock(Station.class), 10);

        // then
        assertThat(line.getSections()).isNotEmpty();
    }

    @Test
    void update_null() {
        // given
        final String name = "name";
        final String color = "color";
        final Line line = new Line(name, color);

        // when
        line.update(null, null);

        // then
        assertThat(line.getName()).isEqualTo(name);
        assertThat(line.getColor()).isEqualTo(color);
    }

    @Test
    void update_notnull() {
        // given
        final String name = "name";
        final String color = "color";
        final Line line = new Line(null, null);

        // when
        line.update(name, color);

        // then
        assertThat(line.getName()).isEqualTo(name);
        assertThat(line.getColor()).isEqualTo(color);
    }

    @Test
    void getStations() {
    }

    @Test
    void removeSection() {
    }
}
