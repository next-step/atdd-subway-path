package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;

class LineTest {
    private static final String LINE_NAME = "신분당선";
    private static final String LINE_COLOR = "bg-red";

    private Line line;

    @BeforeEach
    void setup() {
        line = new Line(LINE_NAME, LINE_COLOR);
    }

    @Test
    void createLine() {
        Line line = new Line(LINE_NAME, LINE_COLOR);

        assertThat(this.line).isEqualTo(line);
    }

    @Test
    void updateLine() {
        line.updateLine("새로운 노선", "새로운 색깔");

        assertThat(line.getName()).isEqualTo("새로운 노선");
        assertThat(line.getColor()).isEqualTo("새로운 색깔");
    }

    @Test
    void addSection() {
        line.addSection(new Section(line, Fixtures.판교역, Fixtures.정자역, 10));
        line.addSection(new Section(line, Fixtures.정자역, Fixtures.미금역, 10));

        List<Station> stations = line.getStations();

        assertThat(stations).hasSize(3);
        assertThat(stations).containsExactly(Fixtures.판교역, Fixtures.정자역, Fixtures.미금역);
    }

    @Test
    void getStations() {
        List<Station> stations = line.getStations();

        assertThat(stations).isEmpty();
    }

    @Test
    void removeSection() {
        line.addSection(new Section(line, Fixtures.판교역, Fixtures.정자역, 10));
        line.addSection(new Section(line, Fixtures.정자역, Fixtures.미금역, 10));

        line.deleteSection(Fixtures.미금역.getId());
        List<Station> stations = line.getStations();

        assertThat(stations).hasSize(2);
        assertThat(stations).containsExactly(Fixtures.판교역, Fixtures.정자역);
    }
}
