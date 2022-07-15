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
    void getStations() {
    }

    @Test
    void removeSection() {
    }
}
