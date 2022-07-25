package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LineTest {
    Station 기흥역;
    Station 신갈역;
    Section section;
    Line line;

    @BeforeEach
    void setUp() {
        기흥역 = new Station("기흥역");
        신갈역 = new Station("신갈역");
        section = Section.builder()
                         .upStation(기흥역)
                         .downStation(신갈역)
                         .distance(10).build();
        line = new Line("분당선", "yellow");
        line.addSection(section);
    }

    @Test
    void addSection() {
        // then
         assertThat(line.isContain(section)).isTrue();
    }

    @Test
    void isContain() {
        // when
        boolean result = line.isContain(section);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void getStations() {
        // when
        List<Station> stations = line.getStations();

        // then
        assertThat(stations).containsExactlyInAnyOrder(기흥역, 신갈역);
        assertThat(stations).hasSize(2);
    }

    @Test
    void removeSection() {
        // when
        line.removeSection(section);

        // then
        assertThat(line.isContain(section)).isFalse();
    }
}
