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
    }

    @Test
    void addSection() {
        // when
        line.addSection(section);

        // then
        assertThat(line.getSections().size()).isEqualTo(1);
        assertThat(line.getSections().get(0).getUpStation().getName()).isEqualTo("기흥역");
        assertThat(line.getSections().get(0).getDownStation().getName()).isEqualTo("신갈역");
    }

    @Test
    void getStations() {
        // given
        line.addSection(section);

        // when
        List<Station> stations = line.getStations();

        // then
        assertThat(stations).containsExactlyInAnyOrder(기흥역, 신갈역);
        assertThat(stations).hasSize(2);
    }

    @Test
    void removeSection() {
        // given
        line.addSection(section);

        // when
        line.removeSection(section);

        // then
        assertThat(line.isContain(section)).isFalse();
    }
}
