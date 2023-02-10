package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
    @DisplayName("노선을 생성한다.")
    void createLine() {
        Line line = new Line(LINE_NAME, LINE_COLOR);

        assertThat(this.line).isEqualTo(line);
    }

    @Test
    @DisplayName("노선을 업데이트한다.")
    void updateLine() {
        line.updateLine("새로운 노선", "새로운 색깔");

        assertAll(
            () -> assertThat(line.getName()).isEqualTo("새로운 노선"),
            () -> assertThat(line.getColor()).isEqualTo("새로운 색깔")
        );

    }

    @Test
    @DisplayName("노선에 구간을 추가한다.")
    void addSection() {
        line.addSection(new Section(line, Fixtures.판교역, Fixtures.정자역, 10));
        line.addSection(new Section(line, Fixtures.정자역, Fixtures.미금역, 10));

        List<Station> stations = line.getStations();

        assertAll(
            () -> assertThat(stations).hasSize(3),
            () -> assertThat(stations).containsExactly(Fixtures.판교역, Fixtures.정자역, Fixtures.미금역)
        );
    }

    @Test
    @DisplayName("노선에 속한 역을 가져온다.")
    void getStations() {
        line.addSection(new Section(line, Fixtures.판교역, Fixtures.정자역, 10));
        line.addSection(new Section(line, Fixtures.판교역, Fixtures.미금역, 5));

        List<Station> stations = line.getStations();

        assertAll(
            () -> assertThat(stations).hasSize(3),
            () -> assertThat(stations).containsExactly(Fixtures.판교역, Fixtures.미금역, Fixtures.정자역)
        );
    }

    @Test
    @DisplayName("노선에서 구간을 제거한다")
    void removeSection() {
        line.addSection(new Section(line, Fixtures.판교역, Fixtures.정자역, 10));
        line.addSection(new Section(line, Fixtures.정자역, Fixtures.미금역, 10));

        line.deleteSection(Fixtures.미금역.getId());
        List<Station> stations = line.getStations();

        assertAll(
            () -> assertThat(stations).hasSize(2),
            () -> assertThat(stations).containsExactly(Fixtures.판교역, Fixtures.정자역)
        );
    }
}
